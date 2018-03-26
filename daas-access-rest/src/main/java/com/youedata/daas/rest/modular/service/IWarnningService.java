package com.youedata.daas.rest.modular.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.model.WarnningPo;

import javax.servlet.http.HttpServletResponse;

/**
 * 告警管理信息相关业务
 *
 * @author chengtao
 */
public interface IWarnningService extends IService<WarnningPo> {

    /**
     * 获取列表
     * @param page 页码
     * @param condfields 查询字段
     * @param convalues 查询字段的值
     * @return {
     * 		datas : {[
     *
     * 		]},
     * 		totalCounts : ""
     * }
     * @throws Exception
     */
    Tip getList(Page page, String condfields, String convalues, String searhvalue, Integer order) throws BussinessException;

    /**
     * 获取重复文件列表
     * @param page 页码
     * @param resCode
     * @return {
     * 		datas : {[
     *
     * 		]},
     * 		totalCounts : ""
     * }
     * @throws Exception
     */
    Page getRepeatFilesList(Page page, String resCode, String searhvalue, Integer order) throws BussinessException;

    void resetRepeatFiles(Integer fileId) throws BussinessException;

    Tip insertWarning(Integer taskId,Integer warnningType) throws Exception;
    /**
     * 导出csv文件
     * @param ids
     * @param response
     * @throws Exception
     */
    void export(String ids, String condfields, String condvalues, String searchvalue, HttpServletResponse response) throws Exception;

    Tip getById(Integer id);

    //获得告警概览
    Tip getCount();
    //忽略告警
    Tip warningIgnore(Integer id);
}
