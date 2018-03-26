package com.youedata.daas.rest.modular.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.youedata.daas.rest.modular.model.WarnningPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface WarnningMapper extends BaseMapper<WarnningPo> {

    /**
     * 告警管理信息列表
     * @param page
     * @param map
     * @param searchvalue
     * @return
     */
    List<WarnningPo> selectWarnningList(Page page, @Param("params") Map map, @Param("searchvalue") String searchvalue, @Param("order") Integer order);

    Integer getOvertimeCounts();
    Integer getAtypicalCounts();
    Integer getOutsideCounts();
    Integer getRepeatFilesCounts();
    Integer getErrorstopCounts();

    List<WarnningPo> selectExportWarnningList(@Param("ids") String ids, @Param("params") Map map, @Param("searchvalue") String searchvalue);

    //获取任务状态
    Integer getTaskStatus(@Param("id") Integer id);

    //获取任务性质
    Integer getTaskType(@Param("id") Integer taskId);
}
