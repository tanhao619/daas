package com.youedata.daas.rest.modular.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.youedata.daas.rest.modular.model.DataHistory;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.model.dto.DataAccessDto;
import com.youedata.daas.rest.modular.model.dto.DataListDto;
import com.youedata.daas.rest.modular.model.dto.HistoryDetailDto;
import com.youedata.daas.rest.modular.model.dto.TaskTimeDto;
import javafx.concurrent.Task;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by cdyoue on 2017/11/24.
 */
@Component
public interface TaskMapper extends BaseMapper<TaskPo>{
    /**
     * 任务列表
     * @param page
     * @param map
     * @param searchvalue
     * @return
     */
    List<TaskPo> listPage(Page page, @Param("params") Map map, @Param("searchvalue") String searchvalue);

    /**
     * 新增任务
     * @param taskPo
     * @return
     */
    Integer insertEntity(TaskPo taskPo) throws MySQLIntegrityConstraintViolationException;

    /**
     * 修改任务
     * @param taskPo
     * @return
     * @throws Exception
     */
    Integer updateEntity(TaskPo taskPo) throws Exception;

    /**
     * 获取TaskPo的详情
     * @param id
     * @return
     * @throws Exception
     */
    TaskPo getDetail(Integer id) throws Exception;
    /**
     * 任务情况概览
     * @return
     */
    List<Map> getStatusCount();

    /**
     * 获取所有创建时间
     * @return
     */
    List<TaskTimeDto> getAllCreateTime();

    /**
     * 任务情况概览
     * @return
     */
    List<TaskTimeDto> getPilCount();

    Integer getCount(@Param("time") String time,@Param("status") int status);

    /**
     * 获取每日任务的开始结束时间
     * @return
     */
    List<TaskTimeDto> getArrTime();

    /**
     * 获取截止到现在的异常数量
     * @param time
     * @return
     */
    Integer getTotalExceptionNum(@Param("time") String time);

    /**
     * 任务详情-日志
     * @return
     */
    List<HistoryDetailDto> getDetailInfo(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                    @Param("taskId") Integer taskId);
    /**
     * 任务详情-运行历史
     * @return
     */
    List<HistoryDetailDto> getHistory(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                      @Param("taskId") Integer taskId);

    /**
     * 获取生产信息表的数据
     * @return
     */
    List<DataAccessDto> getDataAccess();

    //从生产信息表获取到目前的总计接入量
    Long getDataAccessTotal();
    //从生产信息表获取日平均数据
    Double getArvData();
    //日接入概览
    List<DataListDto> getDayInfo();
    //从数据集获取数据类型
    String getType(@Param("resId")Integer resId);
    //获取最近30天在数据库没有的日期
    List<DataListDto> getDates();
    List<TaskTimeDto> getTaskDates();
//    //获取最近30天日期
//    List<String> getNearDates();
    List<DataListDto> getEffectInfo();
    /**
     * 新建任务时验证是否有相同数据集和数据源的任务
     * @param resIds
     * @param dsId
     * @return
     * @throws Exception
     */
    public Integer verifyTask(@Param("resIds") String resIds, @Param("dsId") Integer dsId) throws Exception;

    String getDayArr(@Param("time") String time);

    Double getExecAvgNum(@Param("date") String date);
}
