package com.youedata.daas.rest.modular.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotations.TableName;
import com.youedata.daas.rest.modular.model.DataSourcePo;

/**
 * Created by cdyoue on 2017/12/11.
 */
public class TaskVo {

    /*
    主键id
     */

    private Integer id;
    /*
    任务标识
     */
    private String taskCode;

    /*
    任务名称
     */
    private String taskTitle;

    /*
    数据源
     */
    private Integer dsId;

    /*
    目标介质信息(json格式)
     */
    private JSONObject mediumInfo;

    /*
    任务性质（1：周期性  2：一次性）
     */
    private Integer taskType;

    /*
    报错阈值
     */
    private Integer threshold;

    /*
    任务状态(1:启用2:停用 3:报错停用）
     */
    private Integer taskStatus;

    /*
    接入任务创建人
     */
    private String creater;

    /*
    任务开始时间
     */
    private String startTime;

    /*
    任务结束时间
     */
    private String endTime;

    /*
      创建时间
     */
    private String createTime;

    /*
    修改时间
     */
    private String updateTime;
    /*
    最小配置量
     */
    private Integer min;
    /*
    最大配置量
     */
    private Integer max;
    /*
    周期
     */
    private Integer cycle;
    /*
    单位
     */
    private Integer unit;
    /*
    表,文件,api信息
     */
    private JSONArray rule;
    /*
    数据源类型
     */
    private String dsType;

    /**
     * 数据源
     * @return
     */
    private DataSourcePo dataSourcePo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Integer getDsId() {
        return dsId;
    }

    public void setDsId(Integer dsId) {
        this.dsId = dsId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public JSONObject getMediumInfo() {
        return mediumInfo;
    }

    public void setMediumInfo(JSONObject mediumInfo) {
        this.mediumInfo = mediumInfo;
    }

    public DataSourcePo getDataSourcePo() {
        return dataSourcePo;
    }

    public void setDataSourcePo(DataSourcePo dataSourcePo) {
        this.dataSourcePo = dataSourcePo;
    }

    public JSONArray getRule() {
        return rule;
    }

    public void setRule(JSONArray rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "TaskPo{" +
                "id=" + id +
                ", taskCode='" + taskCode + '\'' +
                ", taskTitle='" + taskTitle + '\'' +
                ", dsId=" + dsId +
                ", mediumInfo='" + mediumInfo + '\'' +
                ", taskType=" + taskType +
                ", threshold=" + threshold +
                ", taskStatus=" + taskStatus +
                ", creater='" + creater + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", cycle=" + cycle +
                ", unit=" + unit +
                ", rule='" + rule + '\'' +
                ", dsType='" + dsType + '\'' +
                ", dataSourcePo='" + dataSourcePo + '\'' +
                '}';
    }

}
