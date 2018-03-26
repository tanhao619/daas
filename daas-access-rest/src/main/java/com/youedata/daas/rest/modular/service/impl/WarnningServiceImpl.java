package com.youedata.daas.rest.modular.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.common.*;
import com.youedata.daas.rest.common.filter.userFilter.UserThreadLocal;
import com.youedata.daas.rest.config.DaasMetaConfig;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.dao.TaskMapper;
import com.youedata.daas.rest.modular.dao.WarnningMapper;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.model.WarnningPo;
import com.youedata.daas.rest.modular.model.WarnningVo;
import com.youedata.daas.rest.modular.model.dto.WarningCvsDto;
import com.youedata.daas.rest.modular.model.dto.WarningDto;
import com.youedata.daas.rest.modular.model.dto.WarningOverview;
import com.youedata.daas.rest.modular.model.vo.TaskVo;
import com.youedata.daas.rest.modular.service.ITaskService;
import com.youedata.daas.rest.modular.service.IWarnningService;
import com.youedata.daas.rest.modular.service.entity2dto.WarningEntity2Dto;
import com.youedata.daas.rest.util.ResultUtil;
import com.youedata.daas.rest.util.SuccessResultEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
@Transactional
public class WarnningServiceImpl extends ServiceImpl<WarnningMapper,WarnningPo> implements IWarnningService {

    @Resource
    private WarnningMapper warnningMapper;
    @Resource
    private WarningEntity2Dto warningEntity2Dto;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RestTemplate template;
    @Autowired
    private DaasMetaConfig daasMetaConfig;
    @Autowired
    private ITaskService taskService;

    @Override
    public Tip getList(Page page, String confields, String convalues, String searchvalue, Integer order) throws BussinessException {
        if (!StringUtils.isEmpty(searchvalue) && SearchMatch.isMatch(SearchMatch.REGEX_SEARCHVALUE,searchvalue)){
            throw new BussinessException(BizExceptionEnum.SEARCHVALUE_ERROR);
        }else {
            Map<String, String> condMap = StringUtil.StrsToMap(confields, convalues, WarnningPo.class);
            List<WarnningPo> providers = warnningMapper.selectWarnningList(page, condMap, searchvalue, order);
            List<WarningDto> dtos = providers.stream().map(p -> warningEntity2Dto.lisEentityToDto(p)).collect(Collectors.toList());
            page.setRecords(dtos);
            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(), SuccessResultEnum.SUCCESS.getMessage(), page);
        }
    }

    @Override
    public Page getRepeatFilesList(Page page, String resCode, String searchvalue, Integer order) throws BussinessException {
        return page;
    }

    @Override
    public void resetRepeatFiles(Integer fileId) throws BussinessException {

    }

    /**
     * 新增告警
     */
    @Override
    public Tip insertWarning(Integer taskId,Integer warnningType) throws Exception {
        WarnningPo warnningPo = selectOne(new EntityWrapper<WarnningPo>().eq("taskId", taskId));
        if (null != warnningPo){
            warnningPo.setWarnningType(warnningType);
            updateById(warnningPo);
        }else {
            warnningPo = new WarnningPo();
            TaskPo taskPo = taskMapper.getDetail(taskId);
            if (null == taskPo){
                throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
            }
            warnningPo.setCreateTime(new Date());
            warnningPo.setTaskId(taskId);
            warnningPo.setWarnningType(warnningType);
            warnningPo.setTaskName(taskPo.getTaskTitle());
            warnningPo.setDsType(taskPo.getDsType());
            warnningPo.setMedium(taskPo.getMediumInfo().getString("type"));
            super.insert(warnningPo);
        }
        return ResultUtil.result(SuccessResultEnum.ADD_SUCCESS.getCode(),SuccessResultEnum.ADD_SUCCESS.getMessage());
    }

    /**
     *导出csv文件
     * @throws Exception
     */
    @Override
    public void export(String ids,String condFields,String condValues, String searchvalue, HttpServletResponse response) throws Exception {
        if (!StringUtils.isEmpty(searchvalue) && SearchMatch.isMatch(SearchMatch.REGEX_SEARCHVALUE, searchvalue)) {
            throw new BussinessException(BizExceptionEnum.SEARCHVALUE_ERROR);
        } else {
            //有id传过来时批量下载, 没有id时,全下载
            List<WarnningPo> warnningPoList = null;
            Map<String, String> condMap = StringUtil.StrsToMap(condFields, condValues, WarnningPo.class);
            warnningPoList = warnningMapper.selectExportWarnningList(ids, condMap, searchvalue);
            //文件名称
            String fileName = Constant.WARNNING_FILE_NAME;
            //表头信息
            String lTitle = Constant.WARNNING_COL_NAMES;
            //字段信息, 这里不要写死
            String mapKey = Constant.WARNNING_COL_KEYS;
            //获取需要导出数据的列表
            List dataList = new ArrayList();
            for (WarnningPo warnningPo : warnningPoList) {
                WarningCvsDto warningCvsDto = new WarningCvsDto();
                Integer taskType = warnningMapper.getTaskType(warnningPo.getTaskId());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String str = sdf.format(warnningPo.getCreateTime());
                BeanUtils.copyProperties(warnningPo, warningCvsDto);
                warningCvsDto.setCreateTime(str);
                //任务性质（1：周期  2：一次）
                if (null != taskType && taskType == 1) {
                    warningCvsDto.setTaskType("周期");
                }
                if (null != taskType && taskType == 2) {
                    warningCvsDto.setTaskType("一次");
                }
                Map map = MapObjectUtils.objectToMap(warningCvsDto);
                dataList.add(map);
            }
            final OutputStream os = response.getOutputStream();
            FileUtil.responseSetProperties(fileName, response);
            FileUtil.doExport(dataList, lTitle, mapKey, os);
        }
    }

    @Override
    public Tip getById(Integer id) {
        WarnningPo warnningPo = warnningMapper.selectById(id);
        WarnningVo warnningVo = new WarnningVo();
        if(null == warnningPo){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        TaskPo taskPo = taskMapper.selectById(warnningPo.getTaskId());
        if (null == taskPo){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        BeanUtils.copyProperties(taskPo, warnningVo);
        BeanUtils.copyProperties(warnningPo, warnningVo);
        try {
            Tip tip = taskService.detail(taskPo.getId());
            JSONObject result = new JSONObject((Map<String, Object>) tip.getResult());
            if (null != result.getJSONObject("data")){
                TaskVo taskVo = JSONObject.toJavaObject(result.getJSONObject("data"), TaskVo.class);
                JSONObject mediumInfo = taskVo.getMediumInfo();
                warnningVo.setDataWorks(mediumInfo.getString("groupNames"));
                warnningVo.setDataCollects(mediumInfo.getString("res"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        warnningVo.setTaskId(taskPo.getId());
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),warnningVo);
    }

    @Override
    public Tip getCount() {
        WarningOverview overview = new WarningOverview();
        //未按指定时间开始
//        Integer overtimeCounts = warnningMapper.getOvertimeCounts();
        //未达到传输标准
        Integer outtransportCounts = warnningMapper.getAtypicalCounts();
        //周期内未执行
        Integer outcycleCounts = warnningMapper.getOutsideCounts();
        //重复文件
        Integer repeatFilesCounts = warnningMapper.getRepeatFilesCounts();
        //报错停止
        Integer errorstopCounts = warnningMapper.getErrorstopCounts();
        overview.setOuttransportCounts(outtransportCounts);
        overview.setOutcycleCounts(outcycleCounts);
//        overview.setOvertimeCounts(overtimeCounts);
        overview.setRepeatFilesCounts(repeatFilesCounts);
        overview.setErrorstopCounts(errorstopCounts);
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),overview);
    }

    @Override
    public Tip warningIgnore(Integer id) {
//        WarnningPo warnningPo = warnningMapper.selectById(id);
//        if(null == warnningPo){
//            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
//        }
//        String filePath = warnningPo.getFilePath();
//        String fileName = warnningPo.getFileName();
//        String uri = filePath + "/" + fileName;
//        HttpBinaryDataApi httpBinaryDataService = new HttpBinaryDataService();
//        RestMessage message = httpBinaryDataService.deleteFile("192.168.0.64", 22020, uri);
//        if (message.getCode() == 6001){
//            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),message);
//        }else if (message.getCode() == 6008){
//            throw new BussinessException(BizExceptionEnum.DIR_FILE_ISEMPTY);
//        }
//        else {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
        return null;

    }
}
