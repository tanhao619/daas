package com.youedata.daas.rest.modular.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.youedata.daas.rest.modular.dao.FileInfoMapper;
import com.youedata.daas.rest.modular.model.FileInfoPo;
import com.youedata.daas.rest.modular.service.IFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cdyoue on 2018/1/15.
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfoPo> implements IFileInfoService {
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Override
    public void insertFtpFile(FileInfoPo fileInfoPo) {
        //判断统一数据集下是否有重复文件
        if (fileInfoMapper.isFileExist(fileInfoPo.getResId(),fileInfoPo.getFileName()) == 0) {
            fileInfoPo.setRepeatFile(false);
            fileInfoMapper.insertFile(fileInfoPo);
        } else {
            fileInfoPo.setRepeatFile(true);
            fileInfoMapper.insertRepeatFile(fileInfoPo);
        }
    }

    @Override
    public void insertApiFile(FileInfoPo fileInfoPo) {
        fileInfoPo.setRepeatFile(false);
        fileInfoPo.setFileName(fileInfoPo.getFileName()+ "-" + System.currentTimeMillis());
        fileInfoMapper.insertFile(fileInfoPo);
    }
}
