package com.youedata.daas.rest.modular.model;


import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 接入文件信息实体类
 *
 * @author lucky
 * @create 2017-09-11 13:57
 **/
@TableName("daas_access_storage_files")
public class FileInfoPo implements Serializable {
    private Integer resId;

    private String fileName;

    private String fileType;

    private long fileSize;

    private String filePath;

    private int status;

    private Date createTime;

    private Date updateTime;

    private boolean isRepeatFile;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName=fileName;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType=fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize=fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath=filePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status=status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime=createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime=updateTime;
    }

    public boolean isRepeatFile() {
        return isRepeatFile;
    }

    public void setRepeatFile(boolean repeatFile) {
        isRepeatFile=repeatFile;
    }
}
