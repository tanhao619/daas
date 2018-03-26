package com.youedata.daas.rest.schedule;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.common.DateUtil;
import com.youedata.daas.rest.common.JedisClient;
import com.youedata.daas.rest.common.StringUtil;
import com.youedata.daas.rest.common.enums.AccessType;
import com.youedata.daas.rest.common.enums.DbType;
import com.youedata.daas.rest.common.enums.JobGroup;
import com.youedata.daas.rest.common.enums.TaskStatus;
import com.youedata.daas.rest.common.model.DaasAccessProperties;
import com.youedata.daas.rest.config.DaasCoreConfig;
import com.youedata.daas.rest.database.DatabasePoolConnection;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.dao.TaskMapper;
import com.youedata.daas.rest.modular.model.DataSourcePo;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.service.IQuartzJobDetailService;
import com.youedata.daas.rest.schedule.helper.JobLogUtil;
import com.youedata.daas.rest.util.SpringUtil;
import org.apache.commons.collections.map.HashedMap;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * 表任务
 * Created by cdyoue on 2018/1/3.
 */
public class TableJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(TableJob.class);
    @Autowired
    @Qualifier("tableJob")
    private org.springframework.batch.core.Job tableJob;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private DaasCoreConfig daasCoreConfig;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RestTemplate template;
    @Autowired
    private IQuartzJobDetailService quartzJobDetailService;
    @Autowired
    private JedisClient jedisClient;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //任务实体类
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

        TaskPo taskPo = JSONObject.toJavaObject(JSONObject.parseObject(dataMap.getString("taskPo")), TaskPo.class);
        taskPo.setTaskStatus(TaskStatus.START.getStatus());
        taskPo.setExeTime(DateUtil.getTime());

        JSONObject mediumInfo = taskPo.getMediumInfo();
        Integer targetType = mediumInfo.getInteger("type");
        String mediumTypeStr = JobLogUtil.getAliasType(targetType);
        logger.info("任务名：【" +taskPo.getTaskTitle() +"】,目标介质为" + mediumTypeStr + "任务开始....." + "，定时任务执行时间：" + taskPo.getExeTime() + ", 数据源id=" + taskPo.getDsId());

        JobKey jobKey = JobKey.jobKey(taskPo.getTaskCode(), JobGroup.GROUP.getName());
        try {
            taskMapper.updateEntity(taskPo);
        } catch (Exception e) {
            logger.error("{}",e);
            try {
                quartzJobDetailService.disable(jobKey);
                taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                taskMapper.updateEntity(taskPo);
            } catch (Exception e1) {
                logger.error("{}",e);
                e1.printStackTrace();
            }
            throw new BussinessException(BizExceptionEnum.TASK_EDIT_ERROR);
        }
        DataSourcePo dataSourcePo = JSONObject.toJavaObject(JSONObject.parseObject(dataMap.getString("dataSourcePo")), DataSourcePo.class);
        Iterator<Object> iterator = taskPo.getRule().iterator();
        ExitStatus exitStatus = null;
        //后面生产信息要用到数据集id
        String[] resIds = taskPo.getResIds().split(",");
        int i = 0;
        while (iterator.hasNext()){
            JSONObject json = (JSONObject)iterator.next();
            JSONObject to = json.getJSONObject("to");
            String tableName = json.getJSONObject("from").getString("filename");
            String schames = json.getJSONObject("from").getJSONObject("structure").getString("column");
            String mtableName = to.getString("filename");
            to.remove("filename");
            //获取上次存储的大小
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<JSONObject> dataInfo = template.exchange(daasCoreConfig.getDataInfo() + daasCoreConfig.getMysqlNode() + "/" + daasCoreConfig.getDbName() + "/" + mtableName, HttpMethod.GET, entity, JSONObject.class);

            if (dataInfo.getBody().getInteger("code") != 6001){
                try {
                    quartzJobDetailService.disable(jobKey);
                    taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                    taskMapper.updateEntity(taskPo);
                } catch (Exception e) {
                    logger.error("{}",e);
                    e.printStackTrace();
                }
                throw new BussinessException(BizExceptionEnum.TASK_DATA_INFO_ERROR);
            }

            //查询数据库中该表的条数
            String tableTypeName;
            if(taskPo.getDsType() == DbType.Mysql.getDsType()){
                tableTypeName = DbType.Mysql.getTableTypeName();
            }else if(taskPo.getDsType() == DbType.Oracle.getDsType()){
                tableTypeName = DbType.Oracle.getTableTypeName();
            }else{
                throw new BussinessException(BizExceptionEnum.TASK_REQUEST_RAPRAMS_ERROR);
            }
//            String url = JSONObject.parseObject(StringUtil.Remodeling(dataSourcePo.getDsInfo(), dataSourcePo.getDsType(), dataSourcePo.getEngine()))
//                    .getString("url");
//            String userName = JSONObject.parseObject(dataSourcePo.getDsInfo()).getString("userName");
//            String passWord = JSONObject.parseObject(dataSourcePo.getDsInfo()).getString("passWord");
//            DatabasePoolConnection databasePoolConnection = DatabasePoolConnection.getInstance(tableTypeName,url,userName,passWord);
//            long pagesize = ;
//            DruidPooledConnection connection = null;
//            PreparedStatement pstm = null;
//            try {
//                connection = databasePoolConnection.getConnection(tableTypeName, url, userName, passWord);
//                pstm = connection.prepareStatement("SELECT COUNT(1) FROM " + tableName);
//                ResultSet resultSet = pstm.executeQuery();
//                int count = 0;
//                if(resultSet.next()){
//                    count = resultSet.getInt(1);
//                }
//                //每页加载1w跳数据
//                DaasAccessProperties daasAccessProperties = (DaasAccessProperties) SpringUtil.getBean("daasAccessProperties");
//                //拿到页数
//                long pagecount = daasAccessProperties.getPagecount();
//                pagesize = (count / pagecount) + 1;
//            } catch (SQLException e) {
//                logger.info("{}",e);
//                e.printStackTrace();
//                throw new BussinessException(BizExceptionEnum.TASK_DB_DISPATCH_ERROR);
//            }finally {
//                if(pstm != null){
//                    try {
//                        pstm.close();
//                    } catch (SQLException e) {
//                        logger.info("{}",e);
//                        e.printStackTrace();
//                    }
//                }
//
//                if(connection != null){
//                    try {
//                        connection.close();
//                    } catch (SQLException e) {
//                        logger.info("{}",e);
//                        e.printStackTrace();
//                    }
//                }
//            }
            long pageSize = ((DaasAccessProperties) SpringUtil.getBean("daasAccessProperties")).getPagecount();
            Long blength = dataInfo.getBody().getJSONObject("data").getLong("length");
            Long bspace = dataInfo.getBody().getJSONObject("data").getLong("space");
            //batch传参
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addLong("taskId", Long.valueOf(taskPo.getId()))
                    .addLong("sourceType",Long.valueOf(taskPo.getDsType()))
                    .addString("targetType", taskPo.getMediumInfo().getString("type"))//目标介质类型
                    .addString("dsInfo", StringUtil.Remodeling(dataSourcePo.getDsInfo(), dataSourcePo.getDsType(),dataSourcePo.getEngine()))//数据源链接信息
                    .addString("sql",StringUtil.getSql(json,JSONObject.parseObject(dataSourcePo.getDsInfo()).getString("userName")))
                    .addString("tableName",tableName)
                    .addString("mtableName",mtableName)
                    .addLong("threshold",Long.valueOf(taskPo.getThreshold()))
                    .addLong("unit",Long.valueOf(taskPo.getUnit()))
                    .addLong("min",taskPo.getMin().longValue())
                    .addLong("max",taskPo.getMax().longValue())
                    .addLong("blength",blength)
                    .addLong("bspace",bspace)
                    .addString("to",to.toJSONString())
                    .addString("accessType", AccessType.TABLE.getAccessType())
                    .addString("resId", resIds[i])
                    .addLong("pageSize",pageSize)
                    .addString("tableTypeName",tableTypeName)
                    .addString("schames",schames)
                    .toJobParameters();
            logger.info("sql: " + StringUtil.getSql(json,JSONObject.parseObject(dataSourcePo.getDsInfo()).getString("userName")));
            try {
                exitStatus = jobLauncher.run(tableJob, jobParameters).getExitStatus();
            } catch (Exception e) {
                logger.error("{}",e);
                try {
                    quartzJobDetailService.disable(jobKey);
                    taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                    taskMapper.updateEntity(taskPo);
                } catch (Exception e1) {
                    logger.error("{}",e);
                    e1.printStackTrace();
                }
                throw new JobExecutionException(new BussinessException(BizExceptionEnum.TASK_START_OR_STOP_ERROR));
            }
            if (exitStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode())){
                throw new JobExecutionException(new BussinessException(BizExceptionEnum.TASK_START_OR_STOP_ERROR));
            }
            i++;
        }
    }
}
