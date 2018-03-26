package com.youedata.daas.rest.modular.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.modular.model.TaskPo;
import com.youedata.daas.rest.modular.service.IQuartzJobDetailService;
import com.youedata.daas.rest.modular.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;

/**
 * Created by cdyoue on 2017/12/11.
 */
@Api(value = "task", description = "任务管理")
@RestController
@RequestMapping("/api/v2/daas/access")
public class TaskController {

    protected static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IQuartzJobDetailService quartzJobDetailService;
    /**
     * 任务分页列表
     * @param pagenum
     * @param pagesize
     * @param condfields
     * @param condvalues
     * @param searchvalue
     * @return
     * @throws Exception
     */
    @ApiOperation(value="任务分页列表(完成)", notes="任务分页列表",response = HttpMessage.class)
    @GetMapping("/task/list")
    public Tip listPage(
            @ApiParam(value = "页码。默认第一页(起始索引为1)",defaultValue = "1") @RequestParam(value="pagenum", required=false, defaultValue = "1") Integer pagenum,
            @ApiParam(value = "每页大小,默认为10", defaultValue = "10") @RequestParam(value="pagesize", required=false, defaultValue = "10") Integer pagesize,
            @ApiParam(value = "所有provider实体类对应的属性,两个以上属性用,逗号分开", defaultValue = "proPCode,proCTime") @RequestParam(value="condfields", required=false) String condfields,
            @ApiParam(value = "所有provider实体类对应的属性的值,两个以上属性值用,逗号分开", defaultValue = "65445,2017-10-13") @RequestParam(value="condvalues", required=false) String condvalues,
            @ApiParam(value = "模糊检索(先会根据标题匹配,再根据描述匹配)", defaultValue = "国信") @RequestParam(value = "searchvalue", required = false) String searchvalue
    ) throws Exception{

        Page page=new Page(pagenum,pagesize);
        return taskService.listPage(page, condfields, condvalues, searchvalue);
    }

    /**
     * 任务配置
     * @param taskPo
     * @return
     * @throws Exception
     */
    @ApiOperation(value="任务配置(完成)", notes="任务配置",response = HttpMessage.class)
    @PostMapping("/task/insert")
    public Tip insertEntity(
            @ApiParam(value = "任务创建实体", required = true) @RequestBody TaskPo taskPo
    ) throws Exception{
        return taskService.insertEntity(taskPo);
    }

    /**
     * 修改任务
     * @param taskPo
     * @return
     * @throws Exception
     */
    @ApiOperation(value="修改任务(完成)", notes="修改任务",response = HttpMessage.class)
    @PutMapping("/task/update")
    public Tip updateEntity(
            @ApiParam(value = "任务修改实体", required = true) @RequestBody TaskPo taskPo
    ) throws Exception{
        return taskService.updateEntity(taskPo);
    }

    /**
     * 获取任务详情信息
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation(value="任务详情(完成)", notes="任务详情",response = HttpMessage.class)
    @GetMapping("/task/detail/{id}")
    public Tip detail(
            @ApiParam(value = "任务主键", required = true, defaultValue = "1") @PathVariable(value = "id") Integer id
    ) throws Exception{
        return taskService.detail(id);
    }

    /**
     * 删除任务
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation(value="删除任务(完成)", notes="删除任务",response = HttpMessage.class)
    @DeleteMapping("/task/delete")
    public Tip delete(
            @ApiParam(value = "任务主键id", required = true) @RequestParam(value = "id") Integer id
    ) throws Exception{
        return taskService.delete(id);
    }

    /**
     * 启动/停止任务
     * @param taskId
     * @param state
     * @return
     * @throws Exception
     */
    @ApiOperation(value="执行/停止任务", notes="执行/停止任务",response = HttpMessage.class)
    @PutMapping("/task/state/{taskId}/{state}")
    public Tip state(
            @ApiParam(value = "任务", required = true) @PathVariable Integer taskId,
            @ApiParam(value = "状态", required = true) @PathVariable Integer state
    ) throws Exception{
        return taskService.state(taskId, state);
    }

    /**
     * 获取表名
     * @param resId
     * @return
     * @throws Exception
     */
    @ApiOperation(value="获取表名", notes="获取表名",response = HttpMessage.class)
    @GetMapping("/task/tablename/{resId}")
    public Tip tablename(
            @ApiParam(value = "数据集id", required = true) @PathVariable Integer resId
    ) throws Exception{
        return taskService.getTableName(resId);
    }

    /**
     * 获取目标表信息
     * @param resId
     * @return
     * @throws Exception
     */
    @ApiOperation(value="获取目标表信息", notes="获取目标表信息",response = HttpMessage.class)
    @GetMapping("/task/meta/{resId}")
    public Tip metadata(
            @ApiParam(value = "数据集id", required = true) @PathVariable Integer resId
    ) throws Exception{
        return taskService.getMetaData(resId);
    }

    @ApiOperation(value="验证同数据源同数据集的任务", notes="验证同数据源同数据集的任务",response = HttpMessage.class)
    @GetMapping("/task/verify")
    public Tip verifyTask(
            @ApiParam(value = "数据集id", required = true) @RequestParam String resIds,
            @ApiParam(value = "数据源id", required = true) @RequestParam Integer dsId
    ) throws Exception{
        return taskService.verifyTask(resIds,dsId);
    }
}
