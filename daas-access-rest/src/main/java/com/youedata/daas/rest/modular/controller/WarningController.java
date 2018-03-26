package com.youedata.daas.rest.modular.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.common.Constant;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.model.WarnningVo;
import com.youedata.daas.rest.modular.service.IWarnningService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 告警管理信息控制器
 * @author chengtao
 * @version v2
 */
@Api(value = "warnning", description = "告警管理信息", position = 100, protocols = "http")
@RestController
@RequestMapping("/api/v2/daas/access/warnning")
public class WarningController {

	private Logger logger = LoggerFactory.getLogger(WarningController.class);

	@Autowired
	private IWarnningService warnningService;
	/**
	 * 列表查询
	 *
	 * @param pagenum 起始页数
	 * @param pagesize 页大小
	 * @param condfields 查询的列
	 * @param condvalues 列的值
	 * @Param searchvalue 模糊匹配
	 * @return
	 */
	@ApiOperation(value="告警管理信息列表", notes="告警管理信息列表")
	@ApiResponses({
			@ApiResponse(code = 100, message = "告警管理信息列表")
	})
	@GetMapping(value="")
	public Tip queryList(@ApiParam(value = "页码。默认第一页(起始索引为1)",defaultValue = "")@RequestParam(value="pagenum", required=false, defaultValue = "1") Integer pagenum,
						 @ApiParam(value = "每页大小,默认为10", defaultValue = "")@RequestParam(value="pagesize", required=false, defaultValue = "10") Integer pagesize,
						 @ApiParam(value = "所有Warnning实体类对应的属性,两个以上属性用,逗号分开", defaultValue = "")@RequestParam(value="condfields", required=false) String condfields,
						 @ApiParam(value = "所有Warnning实体类对应的属性的值,两个以上属性值用,逗号分开", defaultValue = "")@RequestParam(value="condvalues", required=false) String condvalues,
						 @ApiParam(value = "模糊检索的值", defaultValue = "")@RequestParam(value = "searchvalue", required = false) String searchvalue,
						 @ApiParam(value = "排序字段（0:升序 1:降序）", defaultValue = "1") @RequestParam(value = "order", required = false) Integer order){

		Page page=new Page(pagenum,pagesize);
		return warnningService.getList(page, condfields, condvalues, searchvalue, order);
	}

	/**
	 * 详情
	 * @param id
	 * @return
	 */
	@ApiOperation(value="告警详情", notes="告警详情")
	@ApiResponses({@ApiResponse(code = 100, message = "告警详情")})
	@GetMapping("/{id}")
	public Tip detail(@ApiParam(value = "告警id",required = true)@PathVariable Integer id){
		return warnningService.getById(id);
	}

	/**
	 * 导出功能
	 * @param response
	 * @param ids
	 */
	@ApiOperation(value="导出告警列表", notes="导出告警列表,传入ids为空时,导出全部")
	@ApiResponses({@ApiResponse(code = 100, message = "导出告警列表")})
	@PatchMapping(value = "")
	public void qcLogExport(HttpServletResponse response, @ApiParam(value = "export导出",defaultValue = "export" ,required = true)@RequestParam(value = "op") String op,
                            @RequestParam(value = "ids", required = false) String ids,
                            @RequestParam(value="condfields", required=false) String condfields,
                            @RequestParam(value="condvalues", required=false) String condvalues,
                            @RequestParam(value = "searchvalue", required = false) String searchvalue){
		try {
			if ("export".equals(op)) {
				warnningService.export(ids, condfields, condvalues, searchvalue, response);
			}
		} catch (Exception e) {
			logger.error(Constant.EXPORT_ERROR,e);
		}

	}

	/**
	 * 告警概览
	 * @return
	 */
	@ApiOperation(value="告警概览", notes="告警概览")
	@ApiResponses({	@ApiResponse(code = 100, message = "告警概览")})
	@GetMapping(value = "/warningOverview")
	public Tip warningOverview(){
		return warnningService.getCount();
	}

	/**
	 * 忽略告警
	 * @return
	 */
	@ApiOperation(value="忽略告警", notes="忽略告警")
	@ApiResponses({	@ApiResponse(code = 100, message = "忽略告警")})
	@PostMapping(value = "/warningIgnore/{id}")
	public Tip warningIgnore(@ApiParam(value = "告警id",required = true)@PathVariable Integer id){
		return warnningService.warningIgnore(id);
	}
}
