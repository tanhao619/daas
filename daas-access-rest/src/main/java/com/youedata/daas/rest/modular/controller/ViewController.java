package com.youedata.daas.rest.modular.controller;

import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.modular.service.IDataSourceService;
import com.youedata.daas.rest.modular.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.web.bind.annotation.*;

/**
 * Created by cdyoue on 2017/12/13.
 */
@Api(value = "view", description = "接入概览")
@RestController
@RequestMapping("/api/v2/daas/access")
public class ViewController {

    protected static final Logger logger = LoggerFactory.getLogger(ViewController.class);
    @Autowired
    private IDataSourceService dataSourceService;
    @Autowired
    private ITaskService taskService;
    /**
     * 数据源占比
     * @return
     */
    @ApiOperation(value="数据源占比", notes="数据源占比",response = HttpMessage.class)
    @GetMapping("/view/proportion")
    public Tip proportion() throws Exception{
        return dataSourceService.proportion();
    }

    /**
     * 任务详情-运行历史
     * @return
     */
    @ApiOperation(value="任务详情-运行历史", notes="运行历史",response = HttpMessage.class)
    @GetMapping("/view/history/{taskId}")
    public Tip accessHistory(@ApiParam(value = "任务id",required = true) @PathVariable(required = true) Integer taskId,
                             @ApiParam(value = "开始时间") @RequestParam(required = false) String startTime,
            @ApiParam(value = "结束时间") @RequestParam(required = false) String endTime) throws Exception{
        return taskService.history(startTime, endTime,taskId);
         }
    /**
     * 任务详情-日志
     * @return
     */
    @ApiOperation(value="任务详情-日志", notes="日志",response = HttpMessage.class)
    @GetMapping("/view/accessDailyRecord/{taskId}")
    public Tip accessDailyRecord(@ApiParam(value = "任务id",required = true) @PathVariable(required = true) Integer taskId,
                             @ApiParam(value = "开始时间") @RequestParam(required = false) String startTime,
            @ApiParam(value = "结束时间") @RequestParam(required = false) String endTime) throws Exception{
        return taskService.accessDailyRecord(startTime, endTime,taskId);
         }

    /**
     * 日接入概览
     * @return
     */
    @ApiOperation(value="日接入概览", notes="日接入概览",response = HttpMessage.class)
    @GetMapping("/view/accessHistory")
    public Tip access() throws Exception{
        return taskService.accessHistory();
    }

    /**
     * 任务情况概览(饼图)
     * @return
     */
    @ApiOperation(value="任务情况概览(饼图)", notes="任务情况概览(饼图)",response = HttpMessage.class)
    @GetMapping("/view/taskstatepie")
    public Tip state() throws Exception{
        return taskService.taskStatePie();
    }

    /**
     * 任务情况概览(坐标图)
     * @return
     */
    @ApiOperation(value="任务情况概览(坐标图)", notes="任务情况概览(坐标图)",response = HttpMessage.class)
    @GetMapping("/view/taskstatepil")
    public Tip stateForDay() throws Exception{
        return taskService.taskStatePil();
    }

    /**
     * 任务效果统计
     * @return
     */
    @ApiOperation(value="任务效果统计", notes="任务效果统计",response = HttpMessage.class)
    @GetMapping("/view/effect")
    public Tip effect() throws Exception{
        return taskService.effect();
    }

    /**
     * 任务异常统计
     * @return
     */
    @ApiOperation(value="任务异常统计", notes="任务异常统计",response = HttpMessage.class)
    @GetMapping("/view/exce")
    public Tip exce() throws Exception{
        return taskService.exec();
    }

    /**
     * 首页- 数据接入
     * @return
     */
    @ApiOperation(value="首页-数据接入", notes="首页-数据接入",response = HttpMessage.class)
    @GetMapping("/index/dataAccess")
    public Tip dataAccess() throws Exception{
        return taskService.dataAccess();
    }

    /**
     * 首页- 实时写入
     * @return
     */
    @ApiOperation(value="首页-实时写入", notes="首页-实时写入",response = HttpMessage.class)
    @GetMapping("/index/dataRealtime")
    public Tip realTimeRead() throws Exception{
        return taskService.realTimeRead();
    }
}
