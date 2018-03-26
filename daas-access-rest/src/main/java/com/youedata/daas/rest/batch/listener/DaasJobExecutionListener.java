package com.youedata.daas.rest.batch.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.common.DateUtil;
import com.youedata.daas.rest.common.enums.*;
import com.youedata.daas.rest.config.DaasCoreConfig;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.dao.ProdLogMapper;
import com.youedata.daas.rest.modular.dao.TaskMapper;
import com.youedata.daas.rest.modular.model.ProdLogPo;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.model.vo.FtpDsInfoVo;
import com.youedata.daas.rest.modular.service.IQuartzJobDetailService;
import com.youedata.daas.rest.modular.service.IWarnningService;
import com.youedata.ftppool.FtpClientPoolManager;
import com.youedata.ftppool.client.ApachePoolFTPClientImpl;
import com.youedata.ftppool.client.FtpInfo;
import org.apache.commons.net.ftp.FTPFile;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Created by cdyoue on 2017/12/25.
 */
public class DaasJobExecutionListener extends JobExecutionListenerSupport {

    @Autowired
    private DaasCoreConfig daasCoreConfig;
    @Autowired
    private IWarnningService warnningService;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ProdLogMapper prodLogMapper;
    @Autowired
    private FtpClientPoolManager ftpClientPoolManager;
    @Autowired
    private IQuartzJobDetailService quartzJobDetailService;
    @Autowired
    private RestTemplate template;
    private Logger logger = LoggerFactory.getLogger(DaasJobExecutionListener.class);
    int i = 0;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobParameters jobParameters = jobExecution.getJobParameters();
        String to = jobParameters.getString("to");
        String taskId = jobParameters.getString("taskId");
        logger.info("任务执行开始时间: " + DateUtil.getTime() + ",to=" + to + ",taskId=" + taskId);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        i++;
        JobParameters jobParameters = jobExecution.getJobParameters();
        String to = jobParameters.getString("to");
        String taskId = jobParameters.getString("taskId");
        logger.info("任务执行完成时间: " + DateUtil.getTime() + ",to=" + to + ",taskId=" + taskId);
        if (DataSourceType.FILE.getType() == jobParameters.getLong("dsType").intValue() && jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            String dsInfo = jobParameters.getString("dsInfo");
            FtpDsInfoVo ds = JSON.parseObject(dsInfo, FtpDsInfoVo.class);
            if (ds == null) {
                throw new BussinessException(BizExceptionEnum.FTP_DS_ERROR);
            }
            FtpInfo info = new FtpInfo(ds.getUrl(), ds.getPort(), ds.getUserName(), ds.getPassword());
            ApachePoolFTPClientImpl ftpClient = null;
            try {
                ftpClient = new ApachePoolFTPClientImpl(ftpClientPoolManager.getFtpClient(info));
            } catch (Exception e) {
                throw new BussinessException(BizExceptionEnum.TASK_START_ERROR);
            }
            ftpClient.setInfo(info);
            FTPFile[] ftpFiles = ftpClient.listFiles(jobParameters.getString("filePath"));
            String name = ftpFiles[0].getName();
            try {
                ftpClient.deleteFile(jobParameters.getString("filePath") + "/" + name);
            } catch (IOException e) {
                logger.error("{}", e);
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity entity = new HttpEntity(headers);
        TaskPo taskPo = null;
        try {
            taskPo = taskMapper.getDetail(jobParameters.getLong("taskId").intValue());
            //停止实时任务
            if (i == taskPo.getRule().size() && taskPo.getTaskType().equals(TaskType.SINGLE.getType())) {
                JobKey jobKey = JobKey.jobKey(taskPo.getTaskCode(), JobGroup.GROUP.getName());
                quartzJobDetailService.disable(jobKey);
                taskPo.setTaskStatus(TaskStatus.STOP.getStatus());
                taskMapper.updateEntity(taskPo);
                i = 0;
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        ResponseEntity<JSONObject> dataInfoResult = template.exchange(daasCoreConfig.getDataInfo() + daasCoreConfig.getMysqlNode() + "/" + daasCoreConfig.getDbName()
                + "/" + jobParameters.getString("mtableName"), HttpMethod.GET, entity, JSONObject.class);
        taskPo.setEndTime(DateUtil.getTime());
        try {
            taskMapper.updateEntity(taskPo);
        } catch (Exception e) {
            logger.error("{}", e);
            throw new BussinessException(BizExceptionEnum.TASK_EDIT_ERROR);
        }

        Long space = dataInfoResult.getBody().getJSONObject("data").getLong("space");
        Long length = dataInfoResult.getBody().getJSONObject("data").getLong("length");
        try {
            //添加生产信息
            ProdLogPo prodLogPo = new ProdLogPo();
            prodLogPo.setTaskId(taskPo.getId());
            Long records = length - jobParameters.getLong("blength");
            prodLogPo.setRecords(records.intValue());
            prodLogPo.setResId(jobParameters.getString("resId"));
            prodLogPo.setStartTime(taskPo.getExeTime());
            prodLogPo.setEndTime(taskPo.getEndTime());
            //            Long totalSize = space - jobParameters.getLong("bspace");
            //由于daascore那边添加数据太小, 大小显示不出来, 暂时不减去原文件大小
           // Long totalSize = space;
          //  prodLogPo.setTotalSize(totalSize.intValue());
            prodLogPo.setTotalSize(0);
         //   String nowTime = DateUtil.getTime();
           // Long flowrate = totalSize / com.youedata.daas.rest.common.DateUtil.getDifTime(taskPo.getExeTime(), nowTime);
         //   prodLogPo.setFlowrate(flowrate.intValue());

            prodLogMapper.insert(prodLogPo);
            logger.info("添加生产信息成功!");
        } catch (Exception e) {
            logger.error("添加生产信息失败", e);
        }

        //单位为MB的告警
        if (jobParameters.getLong("unit").equals(Long.valueOf(UnitType.MB.getType())) && ((space - jobParameters.getLong("bspace")) / 1024D / 1024D < jobParameters.getLong("min") || (space - jobParameters.getLong("bspace")) / 1024D / 1024D > jobParameters.getLong("max"))) {
            try {
                warnningService.insertWarning(jobParameters.getLong("taskId").intValue(), WarnningType.TRANSMISSION_NOT_REACHED.getType());
            } catch (Exception e) {
                logger.error("告警添加失败", e);
            }
        }
        //单位为条数时的告警
        if (jobParameters.getLong("unit").equals(Long.valueOf(UnitType.ARTICLE.getType())) && ((length - jobParameters.getLong("blength")) < jobParameters.getLong("min") || (length - jobParameters.getLong("blength")) > jobParameters.getLong("max"))) {
            try {
                warnningService.insertWarning(jobParameters.getLong("taskId").intValue(), WarnningType.TRANSMISSION_NOT_REACHED.getType());
            } catch (Exception e) {
                logger.error("告警添加失败", e);
            }
        }
    }
}
