package com.youedata.daas.rest.modular.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.modular.model.DataSourcePo;
import com.youedata.daas.rest.modular.model.vo.DataSourceVo;

/**
 * Created by cdyoue on 2017/11/24.
 */
public interface IDataSourceService extends IService<DataSourcePo>  {
    /**
     * 数据源占比
     * @return
     * @throws Exception
     */
    public Tip proportion() throws Exception;

    /**
     * 数据源管理列表(非分页)
     * @param confields 搜索字段名多个用,逗号分开
     * @param convalues 搜索字段值多个用,逗号分开
     * @param searchvalue 模糊匹配(默认title, 然后desc)
     * @return 列表
     * @throws Exception
     */
    public Tip listAll(String confields, String convalues, String searchvalue) throws Exception;
    /**
     * 数据源管理分页列表
     * @param page 分页信息
     * @param confields 搜索字段名多个用,逗号分开
     * @param convalues 搜索字段值多个用,逗号分开
     * @param searchvalue 模糊匹配(默认title, 然后desc)
     * @return 分页列表
     * @throws Exception
     */
    public Tip listPage(Page page, String confields, String convalues, String searchvalue) throws Exception;


    /**
     * 添加数据源
     * @param dataSourceVo
     * @return
     * @throws Exception
     */
    public Tip insertEntity(DataSourceVo dataSourceVo) throws Exception;
    /**
     * 修改数据源
     */
    public Tip updateEntity(DataSourceVo dataSourceVo) throws Exception;

    /**
     * 获取数据源详情
     * @param id
     * @return
     * @throws Exception
     */
    public Tip detail(Integer id) throws Exception;

    /**
     * 数据源连接测试
     * @param linkJson 数据源链接信息
     * @return
     * @throws Exception
     */
    public Tip link(JSONObject linkJson) throws Exception;

    /**
     * 删除数据源
     * @param id 数据源主键id
     * @return
     * @throws Exception
     */
    public Tip delete(Integer id) throws Exception;

    /**
     * 获取数据源下的表或文件
     * @param dsInfo 数据库连接信息
     * @return
     * @throws Exception
     */
    public Tip files(JSONObject dsInfo ) throws Exception;

    /**
     * 预览表或文件信息
     * @param dsId 数据源
     * @param fileName ftp文件名
     * @return
     * @throws Exception
     */
    public Tip preview(Integer dsId,String fileName) throws Exception;

    Tip viewdetail(Integer dsId,String filePath, String fileName, Integer isHead,String sep) throws Exception;
}
