package com.youedata.daas.rest.modular.service.entity2dto;

import com.youedata.daas.rest.common.enums.EngineType;
import com.youedata.daas.rest.modular.dao.DataSourceMapper;
import com.youedata.daas.rest.modular.model.DataSourcePo;
import com.youedata.daas.rest.modular.model.vo.DataSourceDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by luanyu on 2017/5/20.
 */
@Component
public class DataSourceEntity2Dto {
    @Autowired
    private DataSourceMapper dataSourceMapper;

    public DataSourceDto entityToDto(DataSourcePo p) {
        DataSourceDto dto = new DataSourceDto();
        BeanUtils.copyProperties(p,dto);
        //数据源类型（1,TABLE 2,FILE 3,STREAM）
        if (!StringUtils.isEmpty(p.getDsType()) && p.getDsType().equals(1)){
            dto.setDsType("TABLE");
        }
        if (!StringUtils.isEmpty(p.getDsType()) && p.getDsType().equals(2)){
            dto.setDsType("FILE");
        }
        if (!StringUtils.isEmpty(p.getDsType()) && p.getDsType().equals(3)){
            dto.setDsType("STREAM");
        }
        //  引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
        if (!StringUtils.isEmpty(p.getEngine()) && p.getEngine().equals(EngineType.ORACLE.getType())){
            dto.setEngine("ORACLE");
        }
        if (!StringUtils.isEmpty(p.getEngine()) && p.getEngine().equals(EngineType.MYSQL.getType())){
            dto.setEngine("MYSQL");
        }
        if (!StringUtils.isEmpty(p.getEngine()) && p.getEngine().equals(EngineType.FTP.getType())){
            dto.setEngine("FTP");
        }
        if (!StringUtils.isEmpty(p.getEngine()) && p.getEngine().equals(EngineType.API.getType())){
            dto.setEngine("API");
        }
        //获取关联任务的数量
        Integer num = dataSourceMapper.getRelateTaskNum(p.getId());
        dto.setUsage(num);
        return dto;
    }
}
