package com.youedata.daas.rest.modular.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.core.httpdev.entity.RestMessage;
import com.youedata.daas.core.httpdev.inf.HttpDataUnitAPI;
import com.youedata.daas.core.httpdev.inf.HttpDataUnitDataApi;
import com.youedata.daas.core.httpdev.service.HttpDataUnitDataService;
import com.youedata.daas.core.httpdev.service.HttpDataUnitService;
import com.youedata.daas.rest.common.DateUtil;
import com.youedata.daas.rest.common.SearchMatch;
import com.youedata.daas.rest.common.StringUtil;
import com.youedata.daas.rest.common.enums.*;
import com.youedata.daas.rest.common.filter.userFilter.UserThreadLocal;
import com.youedata.daas.rest.config.DaasCoreConfig;
import com.youedata.daas.rest.config.DaasMetaConfig;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.dao.DataSourceMapper;
import com.youedata.daas.rest.modular.dao.ProdLogMapper;
import com.youedata.daas.rest.modular.dao.TaskMapper;
import com.youedata.daas.rest.modular.model.DO.JobDO;
import com.youedata.daas.rest.modular.model.DO.JobDetailDO;
import com.youedata.daas.rest.modular.model.DO.TriggerDO;
import com.youedata.daas.rest.modular.model.DataHistory;
import com.youedata.daas.rest.modular.model.DataSourcePo;
import com.youedata.daas.rest.modular.model.ProdLogPo;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.model.dto.DataAccessDto;
import com.youedata.daas.rest.modular.model.dto.DataListDto;
import com.youedata.daas.rest.modular.model.dto.HistoryDetailDto;
import com.youedata.daas.rest.modular.model.dto.TaskTimeDto;
import com.youedata.daas.rest.modular.model.vo.TaskVo;
import com.youedata.daas.rest.modular.service.IDataSourceService;
import com.youedata.daas.rest.modular.service.IQuartzJobDetailService;
import com.youedata.daas.rest.modular.service.ITaskService;
import com.youedata.daas.rest.modular.service.IWarnningService;
import com.youedata.daas.rest.util.ResultUtil;
import com.youedata.daas.rest.util.SuccessResultEnum;
import org.apache.hadoop.yarn.webapp.ToJSON;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.hadoop.hdfs.server.namenode.ListPathsServlet.df;

/**
 * Created by cdyoue on 2017/11/24.
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskPo> implements ITaskService{
    protected static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private DaasCoreConfig daasCoreConfig;
    @Autowired
    private DaasMetaConfig daasMetaConfig;
    @Autowired
    private DataSourceMapper dataSourceMapper;
    @Autowired
    private IDataSourceService dataSourceService;
    @Autowired
    private IQuartzJobDetailService quartzJobDetailService;
    @Autowired
    RestTemplate template;
    @Autowired
    private IWarnningService warnningService;
    @Autowired
    private ProdLogMapper plMapper;

    /**
     * 任务详情-运行历史
     * @return
     */
    @Override
    public Tip history(String startTime, String endTime,Integer taskId) throws Exception {
        List<HistoryDetailDto> result = new ArrayList<>();
        List<HistoryDetailDto> list = taskMapper.getHistory(startTime,endTime,taskId);
        for (int i = 0;i < list.size();i++){
            HistoryDetailDto history = new HistoryDetailDto();
            if (null == list.get(i).getEndTime() || null == list.get(i).getStartTime()){
                continue;
            }
            Long useTime = (list.get(i).getEndTime().getTime() - list.get(i).getStartTime().getTime()) / 1000; //耗时（单位：秒）
            list.get(i).setUseTime(String.valueOf(useTime));
            BeanUtils.copyProperties(list.get(i),history);
            result.add(history);
        }
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),result);
    }

    /**
     * 任务详情-日志
     * @return
     */
    @Override
    public Tip accessDailyRecord(String startTime, String endTime, Integer taskId) {
        List<HistoryDetailDto> result = new ArrayList<>();
        List<HistoryDetailDto> list = taskMapper.getDetailInfo(startTime,endTime,taskId);
        for (int i = 0;i < list.size();i++){
            HistoryDetailDto history = new HistoryDetailDto();
            if (null == list.get(i).getEndTime() || null == list.get(i).getStartTime()){
                continue;
            }
            BeanUtils.copyProperties(list.get(i),history);
            result.add(history);
        }
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),result);
    }

    /**
     * 日接入概览
     * @return
     */
    @Override
    public Tip accessHistory() {
        List<DataListDto> list = taskMapper.getDayInfo();//获取最近30的数据
        List<String> date = getDateList(30);//获取最近30天日期
        JSONObject result = new JSONObject(true);
        if (null == list){
            throw new BussinessException(BizExceptionEnum.NO_OBJECT);
        }
        ComparatorDate c = new ComparatorDate();
        Collections.sort(date, c);
        for (int i = 0; i < date.size(); i++){
            Integer totalLength = 0;
            JSONObject data = new JSONObject(true);
            for (int j = 0; j < list.size(); j++) {
                JSONObject datas = JSON.parseObject(JSON.toJSONString(list.get(j)));
                if (list.get(j).getStartTime().equals(date.get(i))){
                    if (list.get(j).getType().toLowerCase().equals("table")) {
                        data.put("table", datas);
                        totalLength += list.get(j).getLength();
                    }
                    if (list.get(j).getType().toLowerCase().equals("file")) {
                        data.put("file", datas);
                        totalLength += list.get(j).getLength();
                    }
                    if (list.get(j).getType().toLowerCase().equals("stream")) {
                        data.put("stream", datas);
                        totalLength += list.get(j).getLength();
                    }
                }
            }
            data.put("totalLength", String.valueOf(totalLength));
            result.put(date.get(i),data);
        }
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),result);
    }

    /**
     * 首页- 数据接入
     * @return
     */
    @Override
    public Tip dataAccess() {
        JSONObject json = new JSONObject();
        List<JSONObject> json2 = new ArrayList<>();
        List<DataAccessDto> data = taskMapper.getDataAccess();
        List<String> dates = getDateList(30);//获取最近30天日期
        ComparatorDate c = new ComparatorDate();
        Collections.sort(dates, c);
        if (data.size() < 1){
            throw new BussinessException(BizExceptionEnum.NO_OBJECT);
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        for (String date:dates){
            JSONObject json1 = new JSONObject();
            Long yAxis = 0L;
            for (DataAccessDto dd:data){
                try {
                    if (sf.parse(date).getTime() == sf.parse(dd.getxAxis()).getTime()){
                        yAxis = dd.getyAxis();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            json1.put("xAxis",date);
            json1.put("yAxis",yAxis);
            json2.add(json1);
        }
        Long totalCount = taskMapper.getDataAccessTotal();
        json.put("totalCount",totalCount);
        json.put("data",json2);
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    /**
     * 首页- 实时写入
     * @return
     */
    @Override
    public Tip realTimeRead() {
        JSONObject json = new JSONObject();
        Double arvData = taskMapper.getArvData();
        DecimalFormat df = new DecimalFormat("######0.00");
        if (arvData == null || arvData == 0){
            arvData = 0.00;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("accessToken", UserThreadLocal.get().getAccessToken());
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<JSONObject> data = template.exchange(daasCoreConfig.getDataReal(), HttpMethod.GET, entity, JSONObject.class);
        JSONObject obj = (JSONObject) data.getBody().getJSONObject("result").get("data");
        json.put("arvData",df.format(arvData));
        json.put("dataReal",obj);
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    @Override
    public Tip getTableName(Integer resId) throws Exception {
        if (resId == null){
            throw new BussinessException(BizExceptionEnum.TASK_RES_HAVE_ERROR);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("accessToken",UserThreadLocal.get().getAccessToken());
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<JSONObject> exchange = template.exchange(daasMetaConfig.getResDetail() + resId, HttpMethod.GET, entity, JSONObject.class);
        if (exchange.getBody().getInteger("code") != 200){
            throw new BussinessException(BizExceptionEnum.TASK_RES_DETAIL_ERROR);
        }
        if (!exchange.getBody().getJSONObject("result").getJSONObject("data").getJSONObject("resType").getInteger("medium").equals(MediumType.MYSQL.getType())){
            throw new BussinessException(BizExceptionEnum.TASK_RES_TYPE_ERROR);
        }
        String uri = exchange.getBody().getJSONObject("result").getJSONObject("data").getString("uri");
        JSONObject json = new JSONObject();
        String[] split = uri.split("/");
        List<String> returnList = new ArrayList<>();
        returnList.add(split[split.length-1]);
        json.put("tablenames",returnList);
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    @Override
    public Tip getMetaData(Integer resId) throws Exception {
        if (resId == null){
            throw new BussinessException(BizExceptionEnum.TASK_RES_HAVE_ERROR);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("accessToken",UserThreadLocal.get().getAccessToken());
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<JSONObject> exchange = template.exchange(daasMetaConfig.getResDetail() + resId, HttpMethod.GET, entity, JSONObject.class);
        if (exchange.getBody().getInteger("code") != 200){
            throw new BussinessException(BizExceptionEnum.TASK_RES_DETAIL_ERROR);
        }
        String uri = exchange.getBody().getJSONObject("result").getJSONObject("data").getString("uri");
        HttpDataUnitAPI dataUnitAPI = new HttpDataUnitService();
        RestMessage restMessage = dataUnitAPI.info(daasCoreConfig.getIp(), daasCoreConfig.getPort(), daasCoreConfig.getMysqlNode()+uri);
        if (restMessage.getCode()!= 6001){
            throw new BussinessException(BizExceptionEnum.TASK_MEDIUM_TABLE_ERROR);
        }
        JSONObject metaJson = (new JSONObject((LinkedHashMap) restMessage.getData())).getJSONObject("metaJson");
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),metaJson);
    }

    @Override
    public Tip taskStatePie() throws Exception {
        JSONObject json = new JSONObject();
        //任务状态(1:启用2:停用 3:报错停用）
        List<Map> list = taskMapper.getStatusCount();
        list.stream().forEach(map -> {
            if (map.get("taskStatus").equals(TaskStatus.START.getStatus())){
                json.put("runningTask",map.get("count"));//运行中任务
            }else if (map.get("taskStatus").equals(TaskStatus.STOP.getStatus())){
                json.put("stopedTask",map.get("count"));//暂停的任务
            }else if(map.get("taskStatus").equals(TaskStatus.ERROR.getStatus())){
                json.put("exceptionTask",map.get("count"));//异常任务
            }
        });
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    //对时间进行排序
    class ComparatorDate implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date begin = null;
            Date end = null;
            try {
                begin = sdf.parse(String.valueOf(obj1));
                end = sdf.parse(String.valueOf(obj2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (begin.after(end)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 任务情况概览(坐标图)
     * @return
     */
    @Override
    public Tip taskStatePil() throws Exception {
        //用于返回数据
        JSONObject json = new JSONObject();
        //用于每日的装数据数量
        JSONObject quantityJson = new JSONObject(true);
        //用于装数据数量
        List<JSONObject> quantityList = new ArrayList<JSONObject>();
        //用于装数据时长
        JSONObject durationList = new JSONObject(true);
        List<String> date = getDateList(30);//获取最近30天日期
        ComparatorDate c = new ComparatorDate();
        Collections.sort(date, c);
        //任务状态(1:启用2:停用 3:报错停用）
        List<TaskTimeDto> dayInfo = taskMapper.getPilCount();
        List<TaskTimeDto> arrTime = taskMapper.getArrTime();
        DecimalFormat df = new DecimalFormat("######0.00");
        for(String dt:date){
            Integer run = 0;
            Integer stop = 0;
            Integer exception = 0;
            String arraytime = "0.00";
            for (TaskTimeDto day:dayInfo){
                if (dt.equals(day.getStartTime()) && day.getTaskStatus() == 1){
                    run = day.getCount();
                }
                if (dt.equals(day.getStartTime()) && day.getTaskStatus() == 2){
                    stop = day.getCount();
                }
                if (dt.equals(day.getStartTime()) && day.getTaskStatus() == 3){
                    exception = day.getCount();
                }
            }
            //获取每日任务的开始结束时间
            for (TaskTimeDto arr:arrTime){
                if (dt.equals(arr.getStartTime())){
                    arraytime = df.format(Double.valueOf(arr.getAvrageTime()));
                    break;
                }
            }
            quantityJson.put(dt, new Integer[]{run,stop,exception});
            durationList.put(dt, arraytime);
        }
        quantityList.add(quantityJson);
        //日接入数量
        json.put("quantity",quantityList);
        json.put("duration",durationList);
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    /**
     * 任务异常统计
     * @return
     * @throws Exception
     */
    @Override
    public Tip exec() throws Exception {
        JSONObject json = new JSONObject(true);
        //报错停止
        JSONObject json2 = new JSONObject(true);
        //其他
        JSONObject json3 = new JSONObject(true);
        //均值
        JSONObject json4 = new JSONObject(true);
        List<String> date = getDateList(30);//获取最近30天日期
        ComparatorDate c = new ComparatorDate();
        Collections.sort(date, c);
        List<TaskTimeDto> list = taskMapper.getPilCount();
        DecimalFormat df = new DecimalFormat("######0.00");
        for (String d:date){
            //任务状态(1:启用 2:停用 3:报错停用）
            Integer otherNum = 0;
            Integer errorNum = 0;
            for (TaskTimeDto dto : list){
                if (d.equals(dto.getStartTime())) {
                    if (dto.getTaskStatus().equals(TaskStatus.ERROR.getStatus())) {
                        errorNum += dto.getCount();
                    } else if (!dto.getTaskStatus().equals(TaskStatus.ERROR.getStatus())) {
                        otherNum += dto.getCount();
                    }
                }
            }
            json2.put(d,String.valueOf(errorNum));
            json3.put(d,String.valueOf(otherNum));
            Double avgNum = taskMapper.getExecAvgNum(d);
            if (null == avgNum){
                avgNum = 0.00;
            }
            json4.put(d,df.format(avgNum));
        }
        json.put("error",json2);
        json.put("other",json3);
        json.put("average",json4);
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    /**
     * 任务效果统计
     * @return
     */
    @Override
    public Tip effect() throws Exception {
        //累计执行量
        JSONObject json1 = new JSONObject(true);
        //日均执行量
        JSONObject json2 = new JSONObject(true);
        //今日执行量
        Integer todayCount = 0;
        JSONObject json = new JSONObject(true);
        //获取接口返回数据
        List<DataListDto> list = taskMapper.getEffectInfo();
        if (null == list){
            throw new BussinessException(BizExceptionEnum.NO_OBJECT);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> date = getDateList(30);//获取最近30天日期
        ComparatorDate c = new ComparatorDate();
        Collections.sort(date, c);
        for (int j = 0; j < date.size(); j ++){
            //获取每日累计执行量
            Integer space = 0;
            for (int k = 0; k < list.size(); k ++){
                if (date.get(j).equals(list.get(k).getStartTime())){
                    space = list.get(k).getSpace();
                    break;
                }
            }
            json1.put(date.get(j),String.valueOf(space));//每日累计执行量
        }
            //获取日均执行量
            for (int i = 0;i < date.size();i ++){
                String time = date.get(i);
                String arr = taskMapper.getDayArr(time);
                if (StringUtils.isEmpty(arr)){
                    arr = "0.00";
                }
                DecimalFormat df = new DecimalFormat("######0.00");
                json2.put(date.get(i),df.format(Double.valueOf(arr)));
            }

        //获取今日执行量
        Date now = new Date(System.currentTimeMillis());
        String nowtime = sdf.format(now);
        for (int k = 0; k < list.size(); k ++){
           if (nowtime.equals(list.get(k).getStartTime())){
               todayCount = list.get(k).getSpace();
           }
        }

        json.put("perExc",json1);//累计执行量
        json.put("averageExc",json2);//日均执行量
        json.put("todayExc",String.valueOf(todayCount));//今日执行量
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    @Override
    public Tip listPage(Page page, String confields, String convalues, String searchvalue) throws Exception {
        if (!StringUtils.isEmpty(searchvalue) && SearchMatch.isMatch(SearchMatch.REGEX_SEARCHVALUE,searchvalue)){
            throw new BussinessException(BizExceptionEnum.SEARCHVALUE_ERROR);
        }else {
            Map<String, String> condMap = StringUtil.StrsToMap(confields, convalues, TaskPo.class);
            List<TaskPo> taskPos = taskMapper.listPage(page, condMap, searchvalue);
            page.setRecords(taskPos);
            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(), SuccessResultEnum.SUCCESS.getMessage(), page);
        }
    }

    @Override
    public Tip insertEntity(TaskPo taskPo) throws Exception {
        /*
        设置任务创建时间,修改时间,任务状态默认为2未启动,任务标识,token,数据源类型
         */
        taskPo.setCreateTime(DateUtil.getTime());
        taskPo.setUpdateTime(DateUtil.getTime());
        taskPo.setTaskStatus(TaskStatus.STOP.getStatus());//新增时默认状态为未启动
        taskPo.setTaskCode(UUID.randomUUID().toString().replaceAll("-", ""));
        taskPo.setAccessToken(UserThreadLocal.get().getAccessToken());
        DataSourcePo dataSourcePo = dataSourceMapper.selectById(taskPo.getDsId());
        taskPo.setDsType(dataSourcePo.getDsType());
        logger.info("新增任务信息: ", taskPo);

        /*
        处理rule数据,新建表,新建数据集,修改血缘关系
         */
        //用于存放成功穿件的目标介质表,出现异常全部删除
        List<String> tableList = new ArrayList<String>();
        //用于存放成功创建的数据集,出现异常全部删除
        List<Integer> resList = new ArrayList<Integer>();
        try {
            JSONObject mediumInfo = taskPo.getMediumInfo();
            Iterator<Object> iterator = taskPo.getRule().iterator();

            //存放数据集id
            List<String> resIds = new ArrayList<>();

            //多表任务防止只添加一个数据集
            int j= 0;
            while (iterator.hasNext()){
                HttpEntity entity = null;
                ResponseEntity<JSONObject> exchange = null;
                String tableName = null;
                JSONObject next = (JSONObject)iterator.next();
                logger.info("rule规则: ",next.toJSONString());
                //前端传入数据集id为空时才会去新增数据集
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                Integer resId = null;
                if (org.apache.commons.lang3.StringUtils.isBlank(taskPo.getResIds()) || j > 0){
                    j++;
                    if (MediumType.MYSQL.getType().equals(taskPo.getMediumInfo().getInteger("type"))){
                        //创建目标表
                        JSONObject to = next.getJSONObject("to");
                        tableName = to.getString("filename");
                        to.remove("filename");
                        entity = new HttpEntity(to,headers);
                        exchange = template.exchange(daasCoreConfig.getDuCreate() + daasCoreConfig.getMysqlNode() +"/"+daasCoreConfig.getDbName() +"/" + tableName, HttpMethod.POST, entity, JSONObject.class);
                        if (exchange.getBody().getInteger("code") != 6001 && exchange.getBody().getInteger("code") != 6009) {
                            logger.error("建表失败原因: ", exchange.getBody().getInteger("code"));
                            delTables(tableList);
                            delDataSets(resList);
                            throw new BussinessException(BizExceptionEnum.TASK_TABLE_CREATE_ERROR);
                        }
                        logger.info("目标表创建成功: " + tableName);
                        //重新加进来, 存入任务中
                        to.put("filename",tableName);
                        //加入列表中, 失败的时候全部删除
                        tableList.add(tableName);
                    }
                    //创建数据集
                    if (org.apache.commons.lang3.StringUtils.isBlank(taskPo.getResIds())){
                        resId = this.createDataset(taskPo, tableName);
                        if (resId == -1){
                            this.delTables(tableList);
                            this.delDataSets(resList);
                            throw new BussinessException(BizExceptionEnum.TASK_RES_CREATE_ERROR);
                        }
                        logger.info("数据集: " + mediumInfo.getString("res") +tableName + "创建成功!");
                        resList.add(resId);
                        resIds.add(String.valueOf(resId));
                    }
                }else {
                    resId = Integer.valueOf(taskPo.getResIds());
                    resIds.add(String.valueOf(resId));
                }

                //修改数据集上下游血缘信息(只针对目标介质为MySQL的)
                if (mediumInfo.getInteger("type").equals(MediumType.MYSQL.getType())){
                    boolean b = this.updateKins(next, taskPo, dataSourcePo,resId);
                    if (!b){
                        this.delTables(tableList);
                        this.delDataSets(resList);
                        throw new BussinessException(BizExceptionEnum.TASK_KINS_UPDATE_ERROR);
                    }
                }
                logger.info("上下游信息修改成功!");
            }
            taskPo.setResIds(String.join(",",resIds));
            //修改血缘关系
            boolean b = this.updateRelation(dataSourcePo, taskPo);
            if (!b){
                this.delTables(tableList);
                this.delDataSets(resList);
                throw new BussinessException(BizExceptionEnum.TASK_RELATION_CREATE_ERROR);
            }
            logger.info("血缘关系修改成功!");

            //添加任务
            taskMapper.insertEntity(taskPo);

            //设置quartzJob
            setQuartzJob(taskPo, dataSourcePo);
        } catch (DuplicateKeyException e) {
            logger.error("{}",e);
            this.delTables(tableList);
            this.delDataSets(resList);
            throw new BussinessException(BizExceptionEnum.TASK_TITLE_REPEAT);
        }catch (DataIntegrityViolationException e){
            logger.error("{}",e);
            this.delTables(tableList);
            this.delDataSets(resList);
            throw new BussinessException(BizExceptionEnum.TASK_REQUEST_RAPRAMS_ERROR);
        }
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("id",taskPo.getId());
        logger.info("数据集: " + taskPo.getTaskTitle() + " 创建成功!");
        return ResultUtil.result(SuccessResultEnum.ADD_SUCCESS.getCode(),SuccessResultEnum.ADD_SUCCESS.getMessage(),returnJSON);
    }

    @Override
    public Tip updateEntity(TaskPo taskPo) throws Exception {
        //mybatisplus对没改数据的id不会做处理, 我们自己要处理下
        TaskPo validEntity = taskMapper.getDetail(taskPo.getId());
        if (null == validEntity){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        DataSourcePo dataSourcePo = dataSourceMapper.selectById(taskPo.getDsId());
        try {
            taskPo.setDsType(dataSourcePo.getDsType());
            taskPo.setTaskCode(validEntity.getTaskCode());
            taskPo.setUpdateTime(DateUtil.getTime(new Date()));
            taskPo.setMediumInfo(validEntity.getMediumInfo());
            taskPo.setRule(validEntity.getRule());
            taskPo.setAccessToken(UserThreadLocal.get().getAccessToken());
            taskPo.setResIds(validEntity.getResIds());
            taskPo.setFilePath(validEntity.getFilePath());
            taskPo.setFileType(validEntity.getFileType());
            taskPo.setSeparative(validEntity.getSeparative());
            taskPo.setHasHeader(validEntity.getHasHeader());
            this.setQuartzJob(taskPo,dataSourcePo);
            taskMapper.updateEntity(taskPo);
        } catch (DuplicateKeyException e) {
            logger.error("{}",e);
            throw new BussinessException(BizExceptionEnum.TASK_TITLE_REPEAT);
        }catch (DataIntegrityViolationException e){
            logger.error("{}",e);
            throw new BussinessException(BizExceptionEnum.TASK_REQUEST_RAPRAMS_ERROR);
        }
        return ResultUtil.result(SuccessResultEnum.UPDATE_SUCCESS.getCode(),SuccessResultEnum.UPDATE_SUCCESS.getMessage());
    }

    @Override
    public Tip detail(Integer id) throws Exception {
        TaskVo taskVo = new TaskVo();
        TaskPo taskPo = taskMapper.getDetail(id);
        if (null == taskPo){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        DataSourcePo dataSourcePo = dataSourceMapper.selectById(taskPo.getDsId());
        BeanUtils.copyProperties(taskPo,taskVo);
        taskVo.setDataSourcePo(dataSourcePo);
        JSONObject mediumInfo = taskVo.getMediumInfo();

        //获取业务数据组名称
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("accessToken", UserThreadLocal.get().getAccessToken());
        HttpEntity entity = new HttpEntity(headers);

        //mysql源多表, 需要存入多个数据集
        String resIds = taskPo.getResIds();
        String[] split = resIds.split(",");
        List<String> resTitles = new ArrayList<>();
        for (String resId:split) {
            ResponseEntity<JSONObject> exchange = template.exchange(daasMetaConfig.getResDetail() + resId, HttpMethod.GET, entity, JSONObject.class);
            JSONObject result = exchange.getBody().getJSONObject("result");
            JSONObject data = (JSONObject) result.get("data");
            String resTitle = data.getString("resTitle");
            resTitles.add(resTitle);
        }
        mediumInfo.put("res",String.join(",",resTitles));
        taskVo.setMediumInfo(mediumInfo);

        String url = daasMetaConfig.getLabelGet();
        ResponseEntity<JSONObject> exchange = template.exchange(url, HttpMethod.GET, entity, JSONObject.class);
        if (exchange.getBody().getInteger("code") == 200) {
            JSONObject data = exchange.getBody().getJSONObject("result");
            JSONArray array = (JSONArray) data.get("datas");
            for (int i = 0; i < array.size(); i++) {
                JSONObject label = array.getJSONObject(i);
                Integer labelId = label.getInteger("id");
                if (labelId.equals(taskVo.getMediumInfo().getInteger("group"))) {
                    String labelName = label.getString("name");
                    mediumInfo.put("groupNames", labelName);
                    break;
                }
            }
        }
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),taskVo);
    }

    @Override
    public Tip delete(Integer id) throws Exception {
        //mybatisplus对没改数据的id不会做处理, 我们自己要处理下
        TaskPo validEntity = taskMapper.selectById(id);
        if (null == validEntity){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        JobKey jobKey =JobKey.jobKey(validEntity.getTaskCode(), JobGroup.GROUP.getName());
        quartzJobDetailService.remove(jobKey);
        taskMapper.deleteById(id);
        return ResultUtil.result(SuccessResultEnum.DEL_SUCCESS.getCode(),SuccessResultEnum.DEL_SUCCESS.getMessage());
    }

    /**
     * 任务总接口
     * @param id 任务id
     * @param state 状态 1:未启动, 2启动, 3报错停用
     * @return
     * @throws Exception
     */
    @Override
    public Tip state(Integer id, Integer state) throws Exception {
        TaskPo taskPo = taskMapper.getDetail(id);
        //设置报错retry
        final RetryTemplate retryTemplate = new RetryTemplate();
        final SimpleRetryPolicy policy = new SimpleRetryPolicy(taskPo.getThreshold(), Collections.<Class<? extends Throwable>, Boolean>singletonMap(Exception.class, true));
        retryTemplate.setRetryPolicy(policy);
        final RetryCallback<Tip, Exception> retryCallback = new RetryCallback<Tip, Exception>() {
            @Override
            public Tip doWithRetry(RetryContext context) throws Exception {
                return daasJob(taskPo, state);
            }
        };
        // 如果RetryCallback执行出现指定异常, 并且超过最大重试次数依旧出现指定异常的话,就执行RecoveryCallback动作
        final RecoveryCallback<Tip> recoveryCallback = new RecoveryCallback<Tip>() {
            @Override
            public Tip recover(RetryContext context) throws Exception {
                logger.error("{}", context.getLastThrowable());
                //停止任务
                JobKey jobKey = JobKey.jobKey(taskPo.getTaskCode(), JobGroup.GROUP.getName());
                quartzJobDetailService.disable(jobKey);
                //修改任务状态为报错
                taskPo.setTaskStatus(TaskStatus.ERROR.getStatus());
                taskPo.setEndTime(DateUtil.getTime());
                try {
                    taskMapper.updateEntity(taskPo);
                } catch (Exception e) {
                    logger.error("{}",e);
                    throw new BussinessException(BizExceptionEnum.TASK_EDIT_ERROR);
                }
                //添加告警信息
                warnningService.insertWarning(id,WarnningType.STOP_BY_ERROR.getType());
                return ResultUtil.result(BizExceptionEnum.TASK_START_ERROR.getCode(),BizExceptionEnum.TASK_START_ERROR.getMessage());
            }
        };
        return retryTemplate.execute(retryCallback, recoveryCallback);
    }

    /**
     * jdbc任务
     * @param taskPo
     * @param state
     * @return
     * @throws Exception
     */
    private Tip daasJob(TaskPo taskPo, Integer state) throws Exception{
        JobKey jobKey = JobKey.jobKey(taskPo.getTaskCode(), JobGroup.GROUP.getName());
        taskPo.setTaskStatus(state);
        if (state.equals(TaskStatus.START.getStatus())){
            if (taskPo.getTaskType().equals(TaskType.SINGLE.getType())){//实时任务
                quartzJobDetailService.triggerNow(jobKey);
                logger.info("实时任务启动成功");
            }else {//周期任务
                quartzJobDetailService.enable(jobKey);
                logger.info("周期任务启动成功");
            }
            taskMapper.updateEntity(taskPo);
            return ResultUtil.result(SuccessResultEnum.START_SUCCESS.getCode(),SuccessResultEnum.START_SUCCESS.getMessage());
        }else{//停用任务
            quartzJobDetailService.disable(jobKey);
//            if (taskPo.getTaskType().equals(TaskType.CYCLE.getType())){
//                taskPo.setEndTime(DateUtil.getTime());
//                Iterator rules = taskPo.getRule().iterator();
//                if (taskPo.getMediumInfo().getInteger("type").equals(MediumType.MYSQL.getType())){
//                    int k = 0;
//                    String[] split = taskPo.getResIds().split(",");
//                    while (rules.hasNext()){
//                        HttpHeaders headers = new HttpHeaders();
//                        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//                        headers.add("accessToken", UserThreadLocal.get().getAccessToken());
//                        HttpEntity entity = new HttpEntity(headers);
//                        JSONObject next = new JSONObject((Map<String, Object>) rules.next());
//                        String url = daasCoreConfig.getDataInfo() + daasCoreConfig.getMysqlNode() + "/" + daasCoreConfig.getDbName() + "/" + next.getJSONObject("to").getString("filename");
//                        ResponseEntity<JSONObject> dataInfoResult = template.exchange(url, HttpMethod.GET, entity, JSONObject.class);
//                        JSONObject data = dataInfoResult.getBody().getJSONObject("data");
//                        Long space = data.getLong("space");
//                        Integer length = data.getInteger("length");
//                        Long flowrate = 0L;
//                        try {
//                            flowrate = (space/(1024*1024))/ DateUtil.getDifTime(taskPo.getStartTime(),taskPo.getEndTime());
//                        } catch (Exception e) {
//                            logger.error("{}",e);
//                        }
//                        //添加生产信息
//                        ProdLogPo prodLogPo = new ProdLogPo();
//                        prodLogPo.setTaskId(taskPo.getId());
//                        prodLogPo.setRecords(length);
//                        prodLogPo.setResId(split[k]);
//                        prodLogPo.setStartTime(taskPo.getExeTime());
//                        prodLogPo.setEndTime(taskPo.getEndTime());
//                        prodLogPo.setTotalSize(space.intValue());
//                        prodLogPo.setFlowrate(flowrate.intValue());
//
//                        plMapper.insert(prodLogPo);
//                        logger.info("添加生产信息成功!");
//
//                        k++;
//                    }
//                }
//            }
            try {
                taskMapper.updateEntity(taskPo);
            } catch (Exception e) {
                logger.error("{}",e);
                throw new BussinessException(BizExceptionEnum.TASK_EDIT_ERROR);
            }
            return ResultUtil.result(SuccessResultEnum.STOP_SUCCESS.getCode(),SuccessResultEnum.STOP_SUCCESS.getMessage());
        }
    }

    private void setQuartzJob(TaskPo taskPo, DataSourcePo dataSourcePo) throws Exception{
        JobDetailDO jobDetailDO = new JobDetailDO();
        jobDetailDO.setJobDO(this.getJobDO(taskPo,dataSourcePo));
        jobDetailDO.setTriggerDOs(this.getTrigger(taskPo));
        try {
            //添加任务
            quartzJobDetailService.add(jobDetailDO);
            //添加任务暂时暂停
            JobKey jobKey = JobKey.jobKey(taskPo.getTaskCode(), JobGroup.GROUP.getName());
            quartzJobDetailService.disable(jobKey);
        } catch (Exception e) {
            logger.error("{}",e);
            throw new BussinessException(BizExceptionEnum.TASK_QUARTZ_FIRE_ERROR);
        }
    }

    /**
     * 获取triggerDOs
     * @param taskPo
     * @return
     */
    private Set<TriggerDO> getTrigger(TaskPo taskPo){
        Set<TriggerDO> triggerDOs = new HashSet<>();
        TriggerDO triggerDO = new TriggerDO();
        triggerDO.setName(taskPo.getTaskCode());
        triggerDO.setGroup(JobGroup.GROUP.getName());
        triggerDO.setDescription(taskPo.getTaskTitle());
        String startTime = taskPo.getStartTime();
        Integer cycle = taskPo.getCycle();
        String cron;
        if (taskPo.getTaskType().equals(TaskType.SINGLE.getType())){
            cron = com.youedata.daas.rest.util.DateUtil.getCron(new Date(), cycle);
        }else {
            Date date = DateUtil.parseTime(startTime);
            cron = com.youedata.daas.rest.util.DateUtil.getCron(date, cycle);
        }
        triggerDO.setCronExpression(cron);
        triggerDOs.add(triggerDO);
        return triggerDOs;
    }
    private JobDO getJobDO(TaskPo taskPo, DataSourcePo dataSourcePo){
        JobDO jobDO = new JobDO();
        jobDO.setGroup(JobGroup.GROUP.getName());
        jobDO.setName(taskPo.getTaskCode());
        jobDO.setDescription(taskPo.getTaskTitle());
        //设置JobDataMap
        Map<String, Object> extInfo = new HashMap<>();
        if (taskPo.getDsType().equals(DataSourceType.TABLE.getType())){
            extInfo.put("type", JobType.TABLE_JOB.getName());
        }else if (taskPo.getDsType().equals(DataSourceType.FILE.getType())){
            extInfo.put("type", JobType.FILE_JOB.getName());
        }else if (taskPo.getDsType().equals(DataSourceType.STREAM.getType())){
            extInfo.put("type", JobType.STREAM_JOB.getName());
        }
        extInfo.put("taskPo", JSONObject.toJSONString(taskPo));
        extInfo.put("dataSourcePo",JSONObject.toJSONString(dataSourcePo));
        jobDO.setExtInfo(extInfo);
        return jobDO;
    }

    /**
     * 新增生产日志信息
     * @param po
     */
    @Override
    public void insertProdLogEntity(ProdLogPo po) {
        int cnt = plMapper.insert(po);
        if (cnt == 0) {
            throw new BussinessException(BizExceptionEnum.PARAM_ERROR);
        }
    }

    /**
     * 删除已创建的表
     * @param tables
     */
    private void delTables(List<String> tables){
        if (null != tables && tables.size() != 0){
            tables.stream().forEach(table -> {
                HttpDataUnitAPI dataUnitAPI = new HttpDataUnitService();
                RestMessage drop = dataUnitAPI.drop(daasCoreConfig.getIp(), daasCoreConfig.getPort(), daasCoreConfig.getMysqlNode() + "/" + daasCoreConfig.getDbName() + "/" + table);
                logger.info(drop.getMsg());
            });
        }
    }
    /**
     * 删除已创建数据集
     * @param datasets
     */
    private void delDataSets(List<Integer> datasets){
        if (null != datasets && datasets.size() != 0){
            datasets.stream().forEach(resId -> {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                headers.add("accessToken", UserThreadLocal.get().getAccessToken());
                HttpEntity entity = new HttpEntity(headers);
                ResponseEntity<JSONObject> exchange = template.exchange(daasMetaConfig.getResDel() + resId, HttpMethod.DELETE, entity, JSONObject.class);
                logger.info(exchange.getBody().getString("msg"));
            });
        }
    }

    /**
     * 创建数据集(创建失败返回 -1)
     * @param taskPo
     * @param tableName
     * @return
     */
    private Integer createDataset(TaskPo taskPo, String tableName){
        JSONObject json = new JSONObject();
        JSONObject mediumInfo = taskPo.getMediumInfo();
        //业务组
        json.put("groupCode",mediumInfo.getString("group"));

        //数据集名称, 多表时名称+表名为默认名称
        if (taskPo.getDsType().equals(DataSourceType.FILE.getType()) || taskPo.getRule().size() == 1){
            json.put("resTitle",mediumInfo.getString("res"));
        }else {
            json.put("resTitle",mediumInfo.getString("res") +tableName);
        }
        JSONObject resType = new JSONObject();

        //目标介质类型
        resType.put("medium",mediumInfo.getInteger("type"));

        //数据集类型
        if (mediumInfo.getInteger("type").equals(MediumType.MYSQL.getType()) || mediumInfo.getInteger("type").equals(MediumType.HBASE.getType())
                || mediumInfo.getInteger("type").equals(MediumType.HIVE.getType())){
            resType.put("type",DataSourceType.TABLE.getType());
        }else if (mediumInfo.getInteger("type").equals(MediumType.HDFS.getType())){
            resType.put("type",DataSourceType.FILE.getType());
        }
        json.put("resType",resType);

        if (mediumInfo.getInteger("type").equals(MediumType.HDFS.getType())){
            json.put("uri","/"+ taskPo.getCreater() + "/"
                    +taskPo.getTaskCode());
        }else {
            json.put("uri","/"+daasCoreConfig.getDbName()+"/"+tableName);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("accessToken", UserThreadLocal.get().getAccessToken());
        HttpEntity entity = new HttpEntity(json,headers);
        ResponseEntity<JSONObject> exchange = template.exchange(daasMetaConfig.getResCreate(), HttpMethod.POST, entity, JSONObject.class);
        if (exchange.getBody().getInteger("code") == 200){
            return exchange.getBody().getJSONObject("result").getJSONObject("data").getInteger("resId");
        }
        logger.error(mediumInfo.getString("res") + " 数据集创建失败: " + exchange.getBody().getString("message"));
        return -1;
    }

    /**
     * 修改血缘关系
     * @param dataSourcePo
     * @param taskPo
     * @return
     */
    private boolean updateRelation(DataSourcePo dataSourcePo, TaskPo taskPo){
        String[] split = taskPo.getResIds().split(",");
        ResponseEntity<JSONObject> relation = null;
        for (String s:split) {
            JSONObject relationJson = new JSONObject();
            JSONObject mediumInfo = taskPo.getMediumInfo();
            relationJson.put("dsCreateTime", DateUtil.getTime(dataSourcePo.getCreateTime()));
            relationJson.put("dsCreater",dataSourcePo.getCreater());
            relationJson.put("dsId",taskPo.getDsId());
            relationJson.put("dsName",dataSourcePo.getDsTitle());
            relationJson.put("dsType",dataSourcePo.getEngine());
            relationJson.put("target",mediumInfo.getInteger("type"));
            relationJson.put("taskType",taskPo.getTaskType());
            if (null == taskPo.getCycle()){
                relationJson.put("tUpCycle","");
            }else {
                relationJson.put("tUpCycle",taskPo.getCycle());
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.add("accessToken", UserThreadLocal.get().getAccessToken());
            HttpEntity entity = new HttpEntity(relationJson.toJSONString(),headers);
            relation = template.exchange(daasMetaConfig.getRelationPut()+s, HttpMethod.PUT, entity, JSONObject.class);
            if (relation.getBody().getInteger("code") != 200){
                logger.error("血缘关系修改失败: ", relation.getBody().getString("message"));
                return false;
            }
        }
        return true;
    }

    /**
     * 修改上下游信息
     * @param next
     * @param taskPo
     * @param dataSourcePo
     * @return
     * @throws Exception
     */
    private boolean updateKins(JSONObject next,TaskPo taskPo, DataSourcePo dataSourcePo,Integer resId) throws Exception{
        JSONArray kinJson = new JSONArray();
        String sfilename = next.getJSONObject("from").getString("filename");
        ResponseEntity<JSONObject> kins = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("accessToken", UserThreadLocal.get().getAccessToken());

        Tip preview = dataSourceService.viewdetail(dataSourcePo.getId(),taskPo.getFilePath(), sfilename,taskPo.getHasHeader(),taskPo.getSeparative());
        JSONObject test = new JSONObject((HashMap)preview.getResult());
        JSONArray sArray = test.getJSONObject("data").getJSONObject("structure").getJSONArray("column");
        JSONArray tArray = next.getJSONObject("to").getJSONObject("structure").getJSONArray("column");
        for (int i = 0;i<sArray.size();i++){
            JSONObject info = new JSONObject();
            info.put("sFieldMeaning",((JSONObject)sArray.get(i)).getString("description"));
            info.put("sFieldName",((JSONObject)sArray.get(i)).getString("field"));
            info.put("tFieldMeaning",((JSONObject)tArray.get(i+1)).getString("description"));
            info.put("tFieldName",((JSONObject)tArray.get(i+1)).getString("field"));
            kinJson.add(info);
        }
        JSONObject result = new JSONObject();
        result.put("info",kinJson);
        result.put("objId",dataSourcePo.getId());
        result.put("type",1);
        HttpEntity entity = new HttpEntity(result.toJSONString(),headers);
        kins = template.exchange(daasMetaConfig.getKinsPut() + resId, HttpMethod.POST, entity, JSONObject.class);
        if (kins.getBody().getInteger("code") != 200){
            logger.error("上下游信息修改失败!");
            return false;
        }
        return true;
    }

    /**
     * 新建任务时验证是否有相同数据集和数据源的任务
     * @param resIds
     * @param dsId
     * @return
     * @throws Exception
     */
    @Override
    public Tip verifyTask(String resIds, Integer dsId) throws Exception{
        String[] split = resIds.split(",");
        if (split.length != 1){
            throw new BussinessException(BizExceptionEnum.TASK_RES_SINGLE);
        }
        Integer count = taskMapper.verifyTask(resIds, dsId);
        if (count > 0){
            throw new BussinessException(BizExceptionEnum.TASK_DS_RES_REPEAT);
        }else {
            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),count);
        }
    }

    private List<String> getDateList(int day) {
        List<String> list = new ArrayList<>();
        Date now = new Date();
        for (int i = 1; i <= day; i++) {
            Calendar c = Calendar.getInstance();
            c.setTime(now);
            c.add(Calendar.DAY_OF_MONTH, -i);
            String timeStr = dayFormat(c.getTime());
            list.add(timeStr);
        }
        Collections.reverse(list);
        return list;
    }
    public static String dayFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }
}
