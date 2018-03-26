package com.youedata.daas.rest.modular.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.youedata.daas.rest.modular.model.ProdLogPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProdLogMapper extends BaseMapper<ProdLogPo> {

    List<ProdLogPo> getProdLogByTaskAndStartTime(@Param("startTime") String startTime,
                                                 @Param("taskId") int taskId);
}
