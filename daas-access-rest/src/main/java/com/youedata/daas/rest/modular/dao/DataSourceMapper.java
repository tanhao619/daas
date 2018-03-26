package com.youedata.daas.rest.modular.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.youedata.daas.rest.modular.model.DataSourcePo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by cdyoue on 2017/11/24.
 */
@Component
public interface DataSourceMapper extends BaseMapper<DataSourcePo>{

    //获取分页列表
    List<DataSourcePo> getSourcePageList(Page page, @Param("params") Map<String, String> condMap,@Param("searchvalue") String searchvalue);

    //获取关联任务数量
    Integer getRelateTaskNum(@Param("dsId") Integer dsId);

    //获取全部列表（非分页）
    List<DataSourcePo> getSourceAllList(@Param("params") Map<String, String> condMap,@Param("searchvalue") String searchvalue);

    //获取title
    DataSourcePo getByTitle(@Param("dsTitle") String dsTitle);

    List<Map> getDsTypeCount();
//    Map getDsTypeCount(@Param("dsType") String dsType);
}
