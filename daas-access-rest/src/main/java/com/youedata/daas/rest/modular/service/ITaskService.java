package com.youedata.daas.rest.modular.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.modular.model.ProdLogPo;
import com.youedata.daas.rest.modular.model.TaskPo;

/**
 * Created by cdyoue on 2017/11/24.
 */
public interface ITaskService extends IService<TaskPo>  {

    /**
     * 任务详情-运行历史
     * @return
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @throws Exception
     */
    public Tip history(String startTime, String endTime,Integer taskId) throws Exception;

    /**
     * 任务情况概览(饼图)
     * @return
     * @throws Exception
     */
    public Tip taskStatePie() throws Exception;

    /**
     * 任务情况概览(柱状图)
     * @return
     * @throws Exception
     */
    public Tip taskStatePil() throws Exception;

    /**
     * 任务效果统计
     * @return
     * @throws Exception
     */
    public Tip effect() throws Exception;

    /**
     * 任务异常统计
     * @return
     * @throws Exception
     */
    public Tip exec() throws Exception;

    /**
     * 任务管理分页列表
     * @param page 分页信息
     * @param confields 搜索字段名多个用,逗号分开
     * @param convalues 搜索字段值多个用,逗号分开
     * @param searchvalue 模糊匹配(默认title, 然后desc)
     * @return 分页列表
     * @throws Exception
     */
    public Tip listPage(Page page, String confields, String convalues, String searchvalue) throws Exception;

    /**
     * 任务配置
     * @param taskPo
     * @return
     * @throws Exception
     */
    public Tip insertEntity(TaskPo taskPo) throws Exception;
    /**
     * 修改任务
     * @param taskPo
     * @return
     * @throws Exception
     */
    public Tip updateEntity(TaskPo taskPo) throws Exception;

    /**
     * 获取任务详情
     * @param id
     * @return
     * @throws Exception
     */
    public Tip detail(Integer id) throws Exception;

    /**
     * 删除任务
     * @param id 任务主键id
     * @return
     * @throws Exception
     */
    public Tip delete(Integer id) throws Exception;

    /**
     * 启动暂停任务
     * @param id 任务id
     * @param state 状态 1:未启动, 2启动, 3报错停用
     * @return
     * @throws Exception
     */
    public Tip state(Integer id, Integer state) throws Exception;

    /**
     * 日接入概览
     * @return
     * @throws Exception
     */
    Tip accessHistory();

    /**
     * 首页- 数据接入
     * @return
     */
    Tip dataAccess();

    /**
     * 首页- 实时写入
     * @return
     */
    Tip realTimeRead();

    /**
     * 获取表名
     * @param resId
     * @return
     * @throws Exception
     */
    Tip getTableName(Integer resId) throws Exception;

    Tip getMetaData(Integer resId) throws Exception;
    /**
     * 新增生产日志信息
     * @param po
     */
    void insertProdLogEntity(ProdLogPo po);

    /**
     * 任务详情-日志
     */
    Tip accessDailyRecord(String startTime, String endTime, Integer taskId);

    /**
     * 新建任务时验证是否有相同数据集和数据源的任务
     * @param resIds
     * @param dsId
     * @return
     * @throws Exception
     */
    public Tip verifyTask(String resIds, Integer dsId) throws Exception;
}
