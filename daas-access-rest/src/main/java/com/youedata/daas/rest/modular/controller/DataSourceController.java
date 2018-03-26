package com.youedata.daas.rest.modular.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.modular.model.vo.DataSourceVo;
import com.youedata.daas.rest.modular.service.IDataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.web.bind.annotation.*;

/**
 * Created by cdyoue on 2017/12/11.
 */
@Api(value = "dataSource", description = "数据源管理")
@RestController
@RequestMapping("/api/v2/daas/access")
public class DataSourceController {

    protected static final Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    @Autowired
    private IDataSourceService dataSourceService;

    /**
     * 数据源列表(非分页)
     * @param condfields
     * @param condvalues
     * @param searchvalue
     * @return
     * @throws Exception
     */
    @ApiOperation(value="数据源列表(非分页)(完成)", notes="数据源列表(非分页)",response = HttpMessage.class)
    @GetMapping("/datasource/listall")
    public Tip listPage(
            @ApiParam(value = "所有provider实体类对应的属性,两个以上属性用,逗号分开", defaultValue = "dsType") @RequestParam(value="condfields", required=false) String condfields,
            @ApiParam(value = "所有provider实体类对应的属性的值,两个以上属性值用,逗号分开", defaultValue = "1") @RequestParam(value="condvalues", required=false) String condvalues,
            @ApiParam(value = "模糊检索(先会根据标题匹配,再根据描述匹配)", defaultValue = "国信") @RequestParam(value = "searchvalue", required = false) String searchvalue
    ) throws Exception{
        return dataSourceService.listAll(condfields, condvalues, searchvalue);
    }
    /**
     * 数据源分页列表
     * @param pagenum
     * @param pagesize
     * @param condfields
     * @param condvalues
     * @param searchvalue
     * @return
     * @throws Exception
     */
    @ApiOperation(value="数据源分页列表(完成)", notes="数据源分页列表",response = HttpMessage.class)
    @GetMapping("/datasource/list")
    public Tip listPage(@ApiParam(value = "页码。默认第一页(起始索引为1)",defaultValue = "1") @RequestParam(value="pagenum", required=false, defaultValue = "1") Integer pagenum,
            @ApiParam(value = "每页大小,默认为10", defaultValue = "10") @RequestParam(value="pagesize", required=false, defaultValue = "10") Integer pagesize,
            @ApiParam(value = "所有provider实体类对应的属性,两个以上属性用,逗号分开", defaultValue = "dsType") @RequestParam(value="condfields", required=false) String condfields,
            @ApiParam(value = "所有provider实体类对应的属性的值,两个以上属性值用,逗号分开", defaultValue = "1") @RequestParam(value="condvalues", required=false) String condvalues,
            @ApiParam(value = "模糊检索(先会根据标题匹配,再根据描述匹配)", defaultValue = "国信") @RequestParam(value = "searchvalue", required = false) String searchvalue) throws Exception{
        Page page=new Page(pagenum,pagesize);
        return dataSourceService.listPage(page, condfields, condvalues, searchvalue);
    }

    /**
     * 添加数据源
     * @param dataSourceVo
     * @return
     * @throws Exception
     */
    @ApiOperation(value="新增数据源(完成)", notes="新增数据源",response = HttpMessage.class)
    @PostMapping("/datasource/insert")
    public Tip insertEntity(
            @ApiParam(value = "数据源创建实体", required = true) @RequestBody DataSourceVo dataSourceVo
    ) throws Exception{
        return dataSourceService.insertEntity(dataSourceVo);
    }

    /**
     * 修改数据源
     * @param dataSourceVo
     * @return
     * @throws Exception
     */
    @ApiOperation(value="修改数据源(完成)", notes="修改数据源",response = HttpMessage.class)
    @PutMapping("/datasource/update")
    public Tip updateEntity(
            @ApiParam(value = "数据源修改实体", required = true) @RequestBody DataSourceVo dataSourceVo
    ) throws Exception{
        return dataSourceService.updateEntity(dataSourceVo);
    }

    /**
     * 获取数据源详情信息
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation(value="数据源详情(完成)", notes="数据源详情",response = HttpMessage.class)
    @GetMapping("/datasource/detail/{id}")
    public Tip detail(
            @ApiParam(value = "数据源唯一标识", required = true, defaultValue = "1") @PathVariable(value = "id") Integer id
    ) throws Exception{
        return dataSourceService.detail(id);
    }

    /**
     * 数据源连接测试
     * @param json
     * @return
     * @throws Exception
     */
    @ApiOperation(value="数据源连接测试(完成)", notes="数据源连接测试",response = HttpMessage.class)
    @PostMapping("/datasource/link")
    public Tip link(
            @ApiParam(value = "数据源连接信息(1,ORACLE,2,MYSQL,3,FTP,4,API)(字段:url,userName,passWord,engine,port,parentPath)", required = true) @RequestBody JSONObject json
    ) throws Exception{
        return dataSourceService.link(json);
    }

    /**
     * 删除数据源
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation(value="删除数据源(完成)", notes="删除数据源",response = HttpMessage.class)
    @DeleteMapping("/datasource/delete/{id}")
    public Tip delete(
            @ApiParam(value = "数据源主键id", required = true) @PathVariable(value = "id") Integer id
    ) throws Exception{
        return dataSourceService.delete(id);
    }

    /**
     * 获取数据源下的表或文件
     * @param dsInfo
     * @return
     * @throws Exception
     */
    @ApiOperation(value="获取数据源下的表或文件", notes="获取数据源下的表或文件",response = HttpMessage.class)
    @PostMapping("/datasource/files")
    public Tip tables(
            @ApiParam(value = "数据源连接信息", required = true) @RequestBody JSONObject dsInfo
    ) throws Exception{
        return dataSourceService.files(dsInfo);
    }

    /**
     * 预览FTP中文件信息
     * @param fileName
     * @return
     * @throws Exception
     */
    @ApiOperation(value="预览文件或表信息", notes="预览文件或表信息",response = HttpMessage.class)
    @GetMapping("/datasource/preview/{dsId}")
    public Tip preview(
            @ApiParam(value = "数据源", required = true) @PathVariable Integer dsId,
            @ApiParam(value = "ftp文件名", required = true) @RequestParam String fileName
    ) throws Exception{
        return dataSourceService.preview(dsId,fileName);
    }

    /**
     * 详细预览FTP中csv和json文件信息
     * @param fileName
     * @return
     * @throws Exception
     */
    @ApiOperation(value="详细预览FTP中csv和json文件信息", notes="详细预览FTP中csv和json文件信息",response = HttpMessage.class)
    @GetMapping("/datasource/viewdetail/{dsId}")
    public Tip viewdetail(
            @ApiParam(value = "数据源", required = true) @PathVariable Integer dsId,
            @ApiParam(value = "ftp文件名", required = false) @RequestParam(required = false) String fileName,
            @ApiParam(value = "ftp路径", required = false) @RequestParam(required = false) String filePath,
            @ApiParam(value = "表头(1:有,0:没有)", required = true) @RequestParam(required = false) Integer header,
            @ApiParam(value = "分隔符", required = false) @RequestParam(required = false) String sperat
    ) throws Exception{
        return dataSourceService.viewdetail(dsId,filePath,fileName,header,sperat);
    }
}
