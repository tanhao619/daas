package com.youedata.daas.rest.modular.controller;

import com.youedata.daas.rest.common.DateUtil;
import com.youedata.daas.rest.common.Response;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.model.FileInfoPo;
import com.youedata.daas.rest.modular.service.IFileInfoService;
import com.youedata.daas.rest.util.ResponseBuilder;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * fileinfo 路由控制
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
@Api(tags = {"fileinfo"}, value = "fileinfo 相关操作")
@RestController
@RequestMapping("/api/v2/daas/access/core/tasks/files")
public class FileInfoController {

    @Autowired
    private IFileInfoService fileInfoService;

    /**
     * 新增
     * @return
     */
    @RequestMapping(value="", method= RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addFile(@RequestBody FileInfoPo fileInfo) {
        Map<String, Object> returnMap=new HashMap<String, Object>();
        try {
            fileInfo.setCreateTime(new Date());
            fileInfo.setUpdateTime(new Date());
            //fileInfoService.insertApiFile(fileInfo);
            returnMap.put("code","200");
            returnMap.put("msg","操作成功");
            returnMap.put("result","");
        } catch (BussinessException e) {
            returnMap.put("code", BizExceptionEnum.FILE_METADATA_CREATE_ERROR.getCode());
            returnMap.put("msg","操作失败");
            returnMap.put("result","");
            return new ResponseEntity<Map<String, Object>>(returnMap, HttpStatus.OK);
        }
        return new ResponseEntity<Map<String, Object>>(returnMap, HttpStatus.OK);
    }


    @PostMapping("/insert")
    public Response insert(){
        FileInfoPo fileInfo = new FileInfoPo();
//        fileInfo.setFileName("mytest.txt");
//        fileInfo.setFilePath("/P00001/ORP000011324343545555/mytest.txt");
//        fileInfo.setResId(1);
//        fileInfo.setFileSize(23423423);
//        fileInfo.setFileType("txt");
//        fileInfo.setStatus(1);
//        fileInfo.setCreateTime(new Date());
//        fileInfo.setUpdateTime(new Date());
//        fileInfoService.insertFtpFile(fileInfo);
        return ResponseBuilder.newResponse().build();
    }

}
