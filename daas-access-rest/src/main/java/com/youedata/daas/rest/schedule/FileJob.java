package com.youedata.daas.rest.schedule;

import cn.aofeng.threadpool4j.ThreadPool;
import cn.aofeng.threadpool4j.ThreadPoolManager;
import cn.aofeng.threadpool4j.handler.DiscardFailHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.common.JedisClient;
import com.youedata.daas.rest.common.enums.*;
import com.youedata.daas.rest.config.DaasCoreConfig;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.dao.ProdLogMapper;
import com.youedata.daas.rest.modular.dao.TaskMapper;
import com.youedata.daas.rest.modular.model.DataSourcePo;
import com.youedata.daas.rest.modular.model.ProdLogPo;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.model.vo.FtpDsInfoVo;
import com.youedata.daas.rest.modular.service.IQuartzJobDetailService;
import com.youedata.daas.rest.modular.service.IWarnningService;
import com.youedata.daas.rest.runnable.FtpRunnable;
import com.youedata.daas.rest.schedule.helper.JobLogUtil;
import com.youedata.daas.rest.util.DateUtil;
import com.youedata.ftppool.FtpClientPoolManager;
import com.youedata.ftppool.client.ApachePoolFTPClientImpl;
import com.youedata.ftppool.client.FtpInfo;
import com.youedata.ftppool.client.IFTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件任务
 * Created by cdyoue on 2018/1/3.
 */
public class FileJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(FileJob.class);
    @Autowired
    @Qualifier("fileJob")
    private org.springframework.batch.core.Job fileJob;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private DaasCoreConfig daasCoreConfig;
    @Autowired
    private FtpClientPoolManager ftpClientPoolManager;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ProdLogMapper prodLogMapper;
    @Autowired
    private IWarnningService warnningService;
    @Autowired
    private RestTemplate template;
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private IQuartzJobDetailService quartzJobDetailService;

    public static Set<String> ftpRunables = ConcurrentHashMap.<String>newKeySet();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        FTPFileFilter filefilter = (file) -> !file.getName().endsWith(".tmp");

        //任务实体类
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        TaskPo taskPo = JSONObject.toJavaObject(JSONObject.parseObject(dataMap.getString("taskPo")), TaskPo.class);
        taskPo.setExeTime(com.youedata.daas.rest.common.DateUtil.getTime());
        taskPo.setTaskStatus(TaskStatus.START.getStatus());
        Path path = Paths.get(taskPo.getFilePath());

        JSONObject mediumInfo = taskPo.getMediumInfo();
        Integer targetType = mediumInfo.getInteger("type");
        String mediumTypeStr = JobLogUtil.getAliasType(targetType);
        logger.info("任务名：【" + taskPo.getTaskTitle() + "】,目标介质为" + mediumTypeStr + "任务开始....." + "，定时任务执行时间：" + taskPo.getExeTime() + ", path=" + path);

        JobKey jobKey = JobKey.jobKey(taskPo.getTaskCode(), JobGroup.GROUP.getName());
        try {
            taskMapper.updateEntity(taskPo);
        } catch (Exception e) {
            logger.error("任务修改异常：", e);
            try {
                quartzJobDetailService.disable(jobKey);
                taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                taskMapper.updateEntity(taskPo);
            } catch (Exception e1) {
                logger.error("任务执行异常：", e1);
            }
            throw new BussinessException(BizExceptionEnum.TASK_EDIT_ERROR);
        }
        DataSourcePo dataSourcePo = JSONObject.toJavaObject(JSONObject.parseObject(dataMap.getString("dataSourcePo")), DataSourcePo.class);

        String dsInfo = dataSourcePo.getDsInfo();
        FtpDsInfoVo ds = JSON.parseObject(dsInfo, FtpDsInfoVo.class);
        IFTPClient ftpClient = null;
        //通过ftp连接池获取ftpclient连接
        try {
            logger.info("开始获取ftpClient。。。。。。");
            FtpInfo info = new FtpInfo(ds.getUrl(), ds.getPort(), ds.getUserName(), ds.getPassword());
            ftpClient = new ApachePoolFTPClientImpl(ftpClientPoolManager.getFtpClient(info));
            ftpClient.setFtpClientManager(ftpClientPoolManager);
            ftpClient.setInfo(info);
            if (null == ftpClient) {
                logger.error("获取ftpclient客户端失败！ftpinfo = " + info.toString());
            }
            logger.info("获取的ftpClient = " + ftpClient);
        } catch (Exception e) {
            logger.error("ftpClient获取异常：", e);
            try {
                quartzJobDetailService.disable(jobKey);
                taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                taskMapper.updateEntity(taskPo);
            } catch (Exception e1) {
                logger.error("停止任务异常:", e);
            }
        }
        //获取上次存储的大小
        FTPFile[] files = ftpClient.listFiles(path.toString().replaceAll("\\\\", "/"), filefilter);
        if (files.length == 0) {
            logger.info("path = " + path + ",未扫描到文件........." + ",定时任务执行时间：" + taskPo.getExeTime());
            //添加生产信息
            ProdLogPo prodLogPo = new ProdLogPo();
            prodLogPo.setTaskId(taskPo.getId());
            prodLogPo.setRecords(0);
            prodLogPo.setResId(String.valueOf(taskPo.getResIds()));
            prodLogPo.setStartTime(taskPo.getExeTime());
            prodLogPo.setEndTime(DateUtil.getTime());
            prodLogPo.setTotalSize(0);
            prodLogPo.setFlowrate(0);
            prodLogMapper.insert(prodLogPo);
            return;
        }
        if (MediumType.MYSQL.getType().equals(targetType)) {
            try {
                JSONArray array = taskPo.getRule();
                JSONObject jsonTo = (JSONObject) array.getJSONObject(0).get("to");
                String tableName = jsonTo.getString("filename");
                jsonTo.remove("filename");
                //创建mysql表
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity entity = new HttpEntity(headers);
                //获取上次存储的大小
                ResponseEntity<JSONObject> dataInfo = template.exchange(daasCoreConfig.getDataInfo() + daasCoreConfig.getMysqlNode() + "/" + daasCoreConfig.getDbName() + "/" + tableName, HttpMethod.GET, entity, JSONObject.class);

                if (dataInfo.getBody().getInteger("code") != 6001) {
                    try {
                        quartzJobDetailService.disable(jobKey);
                        taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                        taskMapper.updateEntity(taskPo);
                    } catch (Exception e1) {
                        logger.error("{}", e1);
                        e1.printStackTrace();
                    }
                    throw new BussinessException(BizExceptionEnum.TASK_DATA_INFO_ERROR);
                }
                Long blength = dataInfo.getBody().getJSONObject("data").getLong("length");
                Long bspace = dataInfo.getBody().getJSONObject("data").getLong("space");
                JSONArray jsonArray = jsonTo.getJSONObject("structure").getJSONArray("column");
                String fromClomns = "";
                for (int i = 1; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (i == jsonArray.size() - 1) {
                        fromClomns += jsonObject.getString("field");
                    } else {
                        fromClomns += jsonObject.getString("field") + ",";
                    }
                }

                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addLong("taskId", Long.valueOf(taskPo.getId()))
                        .addString("dsInfo", dataSourcePo.getDsInfo())
                        .addString("fromColumn", fromClomns)
                        .addString("resName", taskPo.getMediumInfo().getString("res"))
                        .addString("mtableName", tableName)
                        .addLong("dsType", Long.valueOf(dataSourcePo.getDsType()))
                        .addLong("unit", Long.valueOf(taskPo.getUnit()))
                        .addLong("min", taskPo.getMin().longValue())
                        .addLong("max", taskPo.getMax().longValue())
                        .addLong("header", Long.valueOf(taskPo.getHasHeader()))
                        .addString("separator", taskPo.getSeparative())
                        .addLong("blength", blength)
                        .addLong("bspace", bspace)
                        .addString("accessToken", taskPo.getAccessToken())
                        .addString("accessType", AccessType.FILE.getAccessType())
                        .addString("taskCode", taskPo.getTaskCode())
                        .addLong("fileType", Long.valueOf(taskPo.getFileType()))
                        .addString("filePath", taskPo.getFilePath())
                        .addString("taskCode", taskPo.getTaskCode())
                        .addLong("fileType", Long.valueOf(taskPo.getFileType()))
                        .addString("filePath", taskPo.getFilePath())
                        .addString("resId", taskPo.getResIds())
                        .addString("to", jsonTo.toString())
                        .toJobParameters();

                ExitStatus exitStatus = jobLauncher.run(fileJob, jobParameters).getExitStatus();
                logger.info("taskId=" + taskPo.getId() + ",任务名=" + taskPo.getTaskTitle() + ",执行结果exitStatus=" + exitStatus.getExitCode());

                if (exitStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                    quartzJobDetailService.disable(jobKey);
                    taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                    taskMapper.updateEntity(taskPo);
                    throw new JobExecutionException(new BussinessException(BizExceptionEnum.TASK_START_OR_STOP_ERROR));
                }
            } catch (Exception e) {
                try {
                    quartzJobDetailService.disable(jobKey);
                    taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                    taskMapper.updateEntity(taskPo);
                } catch (Exception e1) {
                    logger.error("任务执行异常：", e);
                }
            } finally {
                if (ftpClient != null) {
                    try {
                        ftpClient.close();
                    } catch (IOException e) {
                        logger.error("关闭ftp客户端失败" + ",任务信息：" + path.toString() + "-----cause = " + e.getCause() + "-----StackTrace = " + Arrays.toString(e.getStackTrace()));
                        e.printStackTrace();
                    }
                }
            }
        } else if (MediumType.HDFS.getType().equals(targetType)) {
            try {
                Long size = 0L;
                Integer length = files.length;
                int currentHandleJobNum = 0;
                for (FTPFile ftpFile : files) {
                    ThreadPoolManager tpm = ThreadPoolManager.getSingleton();
                    ThreadPool threadPool = tpm.getThreadPool();
                    long currentTaskCounts = threadPool.getTaskCount("default");
                    while (currentTaskCounts >= 10) {
                        logger.info("队列已存在任务数量:" + currentTaskCounts + "\n");
                        currentTaskCounts = threadPool.getTaskCount("default");
                    }

                    if (ftpFile.isFile()) {
                        currentHandleJobNum++;
                        size += ftpFile.getSize();
                        try {
                            JobParameters jobParameters = new JobParametersBuilder()
                                    .addLong("time", System.currentTimeMillis())
                                    .addString("dsInfo", dataSourcePo.getDsInfo())
                                    .addLong("resId", Long.valueOf(taskPo.getResIds()))
                                    .addLong("taskId", Long.valueOf(taskPo.getId()))
                                    .addString("taskCode", taskPo.getTaskCode())
                                    .addString("resName", taskPo.getMediumInfo().getString("res"))
                                    .addString("accessToken", taskPo.getAccessToken())
                                    .addString("creater", taskPo.getCreater())
                                    .addString("totalHandleJobNum", length.toString())
                                    .addString("currentHandleJobNum", String.valueOf(currentHandleJobNum))
                                    .toJobParameters();
                            //判断如果队列中如果存在同一个任务，则不重复添加
                            //if (!ftpRunables.contains(path.resolve(ftpFile.getName()).toString())){
                            logger.info("文件加入任务队列:" + " 路径：" + path.toString() + "\\" + ftpFile.getName());
                            threadPool.submit(new FtpRunnable(path.resolve(ftpFile.getName()), jobParameters), "default", new DiscardFailHandler<Runnable>());
                            //添加当前任务
                            ftpRunables.add(path.resolve(ftpFile.getName()).toString());
                            // }
                        } catch (Exception e) {
                            quartzJobDetailService.disable(jobKey);
                            taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                            taskMapper.updateEntity(taskPo);
                        }
                    }
                }
                //未达到传输标准告警
                if (taskPo.getUnit().equals(UnitType.MB.getType()) && (size / 1024D / 1024D < taskPo.getMin() || size / 1024D / 1024D > taskPo.getMax())) {
                    try {
                        warnningService.insertWarning(taskPo.getId(), WarnningType.TRANSMISSION_NOT_REACHED.getType());
                    } catch (Exception e) {
                        logger.error("告警添加失败", e);
                    }
                } else if (taskPo.getUnit().equals(UnitType.ARTICLE.getType()) && (length < taskPo.getMin() || length > taskPo.getMax())) {
                    try {
                        warnningService.insertWarning(taskPo.getId(), WarnningType.TRANSMISSION_NOT_REACHED.getType());
                    } catch (Exception e) {
                        logger.error("告警添加失败", e);
                    }
                }
            } catch (Exception e) {
                logger.error("任务执行错误" + " 任务信息：" + path.toString() + "-----cause = " + e.getCause() + "-----StackTrace = " + Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            } finally {
                if (ftpClient != null) {
                    try {
                        ftpClient.close();
                    } catch (IOException e) {
                        logger.error("关闭ftp客户端失败" + ",任务信息：" + path.toString() + "-----cause = " + e.getCause() + "-----StackTrace = " + Arrays.toString(e.getStackTrace()));
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
