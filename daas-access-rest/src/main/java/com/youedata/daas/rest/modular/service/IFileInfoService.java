package com.youedata.daas.rest.modular.service;

import com.baomidou.mybatisplus.service.IService;
import com.youedata.daas.rest.modular.model.FileInfoPo;

/**
 * FileInfo服务类
 *
 * @author lucky
 * @create 2017-09-11 14:28
 **/
public interface IFileInfoService extends IService<FileInfoPo> {

    public void insertFtpFile(FileInfoPo fileInfoPo) throws Exception;

    public void insertApiFile(FileInfoPo fileInfoPo) throws Exception;

}
