package com.youedata.daas.rest.runnable;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.youedata.daas.core.httpdev.entity.RestMessage;
import com.youedata.daas.core.httpdev.inf.HttpBinaryDataApi;
import com.youedata.daas.core.httpdev.inf.HttpDataUnitAPI;
import com.youedata.daas.core.httpdev.service.HttpBinaryDataService;
import com.youedata.daas.core.httpdev.service.HttpDataNodeService;
import com.youedata.daas.core.httpdev.service.HttpDataUnitService;
import com.youedata.daas.core.util.DateUtil;
import com.youedata.daas.rest.common.enums.WarnningType;
import com.youedata.daas.rest.config.DaasCoreConfig;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.dao.ProdLogMapper;
import com.youedata.daas.rest.modular.dao.TaskMapper;
import com.youedata.daas.rest.modular.model.FileInfoPo;
import com.youedata.daas.rest.modular.model.ProdLogPo;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.model.vo.FtpDsInfoVo;
import com.youedata.daas.rest.modular.service.IFileInfoService;
import com.youedata.daas.rest.modular.service.IWarnningService;
import com.youedata.daas.rest.schedule.FileJob;
import com.youedata.daas.rest.util.SpringUtil;
import com.youedata.ftppool.FtpClientPoolManager;
import com.youedata.ftppool.client.ApachePoolFTPClientImpl;
import com.youedata.ftppool.client.FtpInfo;
import com.youedata.ftppool.client.IFTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Ftp上传任务执行类
 *
 * @author lucky
 * @create 2017-09-14 16:54
 **/
public class FtpRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FtpRunnable.class);

    private IFileInfoService fileInfoService = (IFileInfoService) SpringUtil.getBean("fileInfoServiceImpl");
    private IWarnningService warnningService = (IWarnningService) SpringUtil.getBean("warnningServiceImpl");

    private FtpClientPoolManager ftpClientPoolManager = (FtpClientPoolManager) SpringUtil.getBean("ftpClientPoolManager");

    private DaasCoreConfig daasCoreConfig = (DaasCoreConfig) SpringUtil.getBean("daasCoreConfig");

    private TaskMapper taskMapper = (TaskMapper) SpringUtil.getBean("taskMapper");

    private ProdLogMapper prodLogMapper = (ProdLogMapper) SpringUtil.getBean("prodLogMapper");

    private Path path;
    private JobParameters jobParameters;

    Object obj = new Object();

    public FtpRunnable(Path path, JobParameters jobParameters) {
        this.path = path;
        this.jobParameters = jobParameters;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.path.toString().hashCode();
    }

    @Override
    public void run() {
        runTask();
    }

    public void runTask() {
        if (jobParameters != null) {
            IFTPClient ftpClient = null;
            InputStream inputStream = null;
            Long fileSize = 0l;
            String dsInfo = jobParameters.getString("dsInfo");
            FtpDsInfoVo ds = JSON.parseObject(dsInfo, FtpDsInfoVo.class);
            try {
                FtpInfo info = new FtpInfo(ds.getUrl(), ds.getPort(), ds.getUserName(), ds.getPassword());
                ftpClient = new ApachePoolFTPClientImpl(ftpClientPoolManager.getFtpClient(info));
                ftpClient.setFtpClientManager(ftpClientPoolManager);
                ftpClient.setInfo(info);
                fileSize = ftpClient.getFileSize(path.toString().replaceAll("\\\\", "/"));
                logger.info("文件正在处理：path=" + path + ", fileSize=" + fileSize);
                inputStream = ftpClient.read(path.toString().replaceAll("\\\\", "/"));
                if (inputStream == null) {
                    logger.error("读取ftp文件到输入流错误 filePathName = " + path.toString().replaceAll("\\\\", "/"));
                    throw new BussinessException(BizExceptionEnum.FTP_GET_ERROR);
                }
                //上传数据
                String uri = daasCoreConfig.getHdfsNode() + "/" + jobParameters.getString("creater") + "/" + jobParameters.getString("taskCode");
                boolean flag = this.uploadFile(uri, jobParameters.getLong("resId").intValue(), inputStream, fileSize);
                if (flag) {
                    FileInfoPo fileInfoPo = new FileInfoPo();
                    fileInfoPo.setFileName(path.getFileName().toString());
                    fileInfoPo.setFilePath(daasCoreConfig.getHdfsNode() + "/" + jobParameters.getString("creater") + "/" + jobParameters.getString("taskCode"));
                    fileInfoPo.setResId(jobParameters.getLong("resId").intValue());
                    fileInfoPo.setFileSize(fileSize);
                    //如果文件没有扩展名，则设置为空
                    fileInfoPo.setFileType(path.getFileName().toString().substring(path.getFileName().toString().lastIndexOf(".") == -1 ?
                            path.getFileName().toString().length() :
                            path.getFileName().toString().lastIndexOf(".") + 1));
                    fileInfoPo.setStatus(1);
                    fileInfoPo.setCreateTime(new Date());
                    fileInfoPo.setUpdateTime(new Date());
                    //存储文件信息
                    fileInfoService.insertFtpFile(fileInfoPo);
                    //删除ftp上文件
                    ftpClient.deleteFile(path.toString().replaceAll("\\\\", "/"));
                    if (fileInfoPo.isRepeatFile()) {
                        try {
                            warnningService.insertWarning(jobParameters.getLong("taskId").intValue(), WarnningType.REPEAT_FILE.getType());
                        } catch (Exception e) {
                            logger.error("告警添加失败", e);
                        }
                    }
                }

                logger.info(Thread.currentThread().getName() + ":文件存入hdfs成功:" + " 路径：" + uri + "/" + path.getFileName());
                logger.info(Thread.currentThread().getName() + ":文件删除成功:" + " 路径：" + path.toString().replaceAll("\\\\", "/"));
                insertOrUpdateProdLogPo(fileSize);

            } catch (IOException e) {
                logger.error("文件存入hdfs错误:" + " 路径：" + path.getFileName() + ".-----cause = " + e.getCause() + "-----StackTrace = " + Arrays.toString(e.getStackTrace()));
            } catch (Exception e) {
                logger.error("{}", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        ftpClient.completeTransFile();
                    } catch (IOException e) {
                        logger.error("inputStream关闭失败:" + " 路径：" + path.toString().replaceAll("\\\\", "/") + "-----cause = " + e.getCause() + "-----StackTrace = " + Arrays.toString(e.getStackTrace()));
                    }
                }
                //移除当前任务
                FileJob.ftpRunables.remove(path.toString());
                try {
                    ftpClient.close();
                } catch (IOException e) {
                    logger.error("ftpClient关闭失败:" + " 路径：" + path.toString().replaceAll("\\\\", "/") + "-----cause = " + e.getCause() + "-----StackTrace = " + Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }

    private void insertOrUpdateProdLogPo(Long fileSize) throws Exception {
        String taskId = jobParameters.getString("taskId");

        TaskPo taskPo = taskMapper.getDetail(Integer.parseInt(taskId));
        int totalHandleJobNum = Integer.parseInt(jobParameters.getString("totalHandleJobNum"));
        int currentHandleJobNum = Integer.parseInt(jobParameters.getString("currentHandleJobNum"));

        String nowTime = DateUtil.getTime();
        if (totalHandleJobNum == currentHandleJobNum) {
            taskPo.setEndTime(nowTime);
            taskMapper.updateEntity(taskPo);

            logger.info("任务处理完成 taskid =" + taskId + ", totalHandleJobNum = " + totalHandleJobNum + ", currentHandleJobNum=" + currentHandleJobNum);
        } else {
            logger.info("任务正在处理中 taskid =" + taskId + ", totalHandleJobNum=" + totalHandleJobNum + ", currentHandleJobNum=" + currentHandleJobNum);
        }

        if (taskPo != null) {
            Long flowrate = fileSize / com.youedata.daas.rest.common.DateUtil.getDifTime(taskPo.getExeTime(), nowTime);
            List<ProdLogPo> prodLogPos = prodLogMapper.getProdLogByTaskAndStartTime(taskPo.getExeTime(), taskPo.getId());
            if (prodLogPos != null && prodLogPos.size() > 0) {
                //修改生产信息
                ProdLogPo prodLogPo = new ProdLogPo();
                prodLogPo.setTaskId(taskPo.getId());
                prodLogPo.setStartTime(taskPo.getExeTime());
                prodLogPo.setEndTime(taskPo.getEndTime());
                prodLogPo.setTotalSize(prodLogPos.get(0).getTotalSize() + fileSize.intValue());
                prodLogPo.setFlowrate(flowrate.intValue());
                prodLogPo.setId(prodLogPos.get(0).getId());
                prodLogMapper.updateById(prodLogPo);
                logger.info("修改生产信息完成!");
            } else {
                //添加生产信息
                ProdLogPo prodLogPo = new ProdLogPo();
                prodLogPo.setTaskId(taskPo.getId());
                prodLogPo.setRecords(0);
                prodLogPo.setResId(String.valueOf(taskPo.getResIds()));
                prodLogPo.setStartTime(taskPo.getExeTime());
                prodLogPo.setEndTime(taskPo.getEndTime());
                prodLogPo.setTotalSize(fileSize.intValue());
                prodLogPo.setFlowrate(flowrate.intValue());
                prodLogMapper.insert(prodLogPo);
                logger.info("添加生产信息完成!");
            }

        }

    }

    /**
     * 上传数据
     *
     * @param uri
     * @param inputStream
     * @param size
     */
    private boolean uploadFile(String uri, Integer resId, InputStream inputStream, long size) {
        //判断是否已经有该文件
        FileInfoPo fileInfoPo = fileInfoService.selectOne(new EntityWrapper<FileInfoPo>()
                .eq("resId", resId)
                .eq("fileName", path.getFileName().toString()));
        if (fileInfoPo == null) {
            //上传数据
            HttpDataNodeService httpDataNodeService = new HttpDataNodeService();
            RestMessage dnMessage = httpDataNodeService.create(daasCoreConfig.getIp(), daasCoreConfig.getPort(), uri, "{\"option\":{\"parent\":\"true\" }}");
            if (dnMessage.getCode() != 6001 && dnMessage.getCode() != 6009) {
                throw new BussinessException(BizExceptionEnum.TASK_DN_CREATE_ERROR);
            }
            HttpDataUnitAPI dataUnitAPI = new HttpDataUnitService();
            RestMessage duMessage = dataUnitAPI.create(daasCoreConfig.getIp(), daasCoreConfig.getPort(), uri + "/" + path.getFileName(), "{\"dunit\":{\"engine\":\"HDFS\",\"type\":\"FILE\"},\"option\":{\"overwrite\":\"false\"}}");
            if (duMessage.getCode() != 6001 && duMessage.getCode() != 6009) {
                throw new BussinessException(BizExceptionEnum.TASK_DU_CREATE_ERROR);
            }
            HttpBinaryDataApi httpBinaryDataService = new HttpBinaryDataService();
            RestMessage putMessage = httpBinaryDataService.uploadInputStream(daasCoreConfig.getIp(), daasCoreConfig.getPort(), "-1", uri + "/" + path.getFileName(), inputStream, null, size, "overwrite");
            if (putMessage.getCode() != 6001 && putMessage.getCode() != 6009) {
                throw new BussinessException(BizExceptionEnum.TASK_DU_PUT_ERROR);
            }
        } else {
            //上传数据
            HttpDataNodeService httpDataNodeService = new HttpDataNodeService();
            RestMessage dnMessage = httpDataNodeService.create(daasCoreConfig.getIp(), daasCoreConfig.getPort(), uri + "/" + daasCoreConfig.getRepeat(), "{\"option\":{\"parent\":\"true\" }}");
            if (dnMessage.getCode() != 6001 && dnMessage.getCode() != 6009) {
                throw new BussinessException(BizExceptionEnum.TASK_DN_CREATE_ERROR);
            }
            HttpDataUnitAPI dataUnitAPI = new HttpDataUnitService();
            RestMessage duMessage = dataUnitAPI.create(daasCoreConfig.getIp(), daasCoreConfig.getPort(), uri + "/" + daasCoreConfig.getRepeat() + "/" + path.getFileName(), "{\"dunit\":{\"engine\":\"HDFS\",\"type\":\"FILE\"},\"option\":{\"overwrite\":\"false\"}}");
            if (duMessage.getCode() != 6001 && duMessage.getCode() != 6009) {
                throw new BussinessException(BizExceptionEnum.TASK_DU_CREATE_ERROR);
            }
            HttpBinaryDataApi httpBinaryDataService = new HttpBinaryDataService();
            RestMessage putMessage = httpBinaryDataService.uploadInputStream(daasCoreConfig.getIp(), daasCoreConfig.getPort(), "-1", uri + "/" + daasCoreConfig.getRepeat() + "/" + path.getFileName(), inputStream, null, size, "overwrite");
            if (putMessage.getCode() != 6001 && putMessage.getCode() != 6009) {
                throw new BussinessException(BizExceptionEnum.TASK_DU_PUT_ERROR);
            }
        }
        return true;
    }
}
