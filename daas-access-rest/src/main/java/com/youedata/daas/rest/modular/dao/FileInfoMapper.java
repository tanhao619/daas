package com.youedata.daas.rest.modular.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.youedata.daas.rest.modular.model.FileInfoPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * FileInfo Mapper
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
@Component
public interface FileInfoMapper extends BaseMapper<FileInfoPo> {

    int isFileExist(@Param("resId") Integer resCode, @Param("fileName") String fileName);

    void insertFile(FileInfoPo fileInfoPo);

    void insertRepeatFile(FileInfoPo fileInfoPo);
}
