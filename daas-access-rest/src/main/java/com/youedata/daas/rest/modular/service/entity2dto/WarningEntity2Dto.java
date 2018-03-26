package com.youedata.daas.rest.modular.service.entity2dto;

import com.youedata.daas.rest.modular.dao.WarnningMapper;
import com.youedata.daas.rest.modular.model.WarnningPo;
import com.youedata.daas.rest.modular.model.dto.WarningDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Tanhao on 2017/12/28.
 */
@Component
public class WarningEntity2Dto {
    @Autowired
    private WarnningMapper warnningMapper;

    public WarningDto lisEentityToDto(WarnningPo p) {
        WarningDto dto = new WarningDto();
        BeanUtils.copyProperties(p,dto);
        Integer status = null;
        Integer type = null;
        if (null != p.getTaskId()){
            status = warnningMapper.getTaskStatus(p.getTaskId());
            type = warnningMapper.getTaskType(p.getTaskId());
        }
        if (null != status){
            dto.setTaskStatus(status);
        }
        if (null != type){
            dto.setTaskType(type);
        }
        return dto;
    }
}
