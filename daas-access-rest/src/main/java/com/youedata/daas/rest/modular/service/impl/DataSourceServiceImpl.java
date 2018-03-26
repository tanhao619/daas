package com.youedata.daas.rest.modular.service.impl;

import au.com.bytecode.opencsv.CSVReader;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.common.Constant;
import com.youedata.daas.rest.common.SearchMatch;
import com.youedata.daas.rest.common.StringUtil;
import com.youedata.daas.rest.common.enums.DataSourceType;
import com.youedata.daas.rest.common.enums.EngineType;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.dao.DataSourceMapper;
import com.youedata.daas.rest.modular.model.DataSourcePo;
import com.youedata.daas.rest.modular.model.vo.DataSourceDto;
import com.youedata.daas.rest.modular.model.vo.DataSourceVo;
import com.youedata.daas.rest.modular.model.vo.FtpDsInfoVo;
import com.youedata.daas.rest.modular.service.IDataSourceService;
import com.youedata.daas.rest.modular.service.entity2dto.DataSourceEntity2Dto;
import com.youedata.daas.rest.util.ResultUtil;
import com.youedata.daas.rest.util.SuccessResultEnum;
import com.youedata.ftppool.FtpClientPoolManager;
import com.youedata.ftppool.client.ApachePoolFTPClientImpl;
import com.youedata.ftppool.client.FtpInfo;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by cdyoue on 2017/11/24.
 */
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSourcePo> implements IDataSourceService {
    protected static final Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    @Autowired
    private DataSourceMapper dataSourceMapper;
    @Autowired
    private DataSourceEntity2Dto dataSourceEntity2Dto;
    @Autowired
    private FtpClientPoolManager ftpClientPoolManager;
    @Autowired
    private RestTemplate template;

    Map<JSONObject,DruidDataSource> connectionMap = new HashMap<>();

    @Override
    public Tip proportion() throws Exception {
        JSONObject json = new JSONObject();
        //数据源类型（1,TABLE 2,FILE 3,STREAM）
        List<Map> list = dataSourceMapper.getDsTypeCount();
        list.stream().forEach(map ->{
            if (map.get(Constant.DSTYPE).equals(DataSourceType.TABLE.getType())){
                json.put(Constant.TABLEDATA,map.get(Constant.TOTAL));
            }else if (map.get(Constant.DSTYPE).equals(DataSourceType.FILE.getType())){
                json.put(Constant.FILEDATA,map.get(Constant.TOTAL));
            }else if (map.get(Constant.DSTYPE).equals(DataSourceType.STREAM.getType())){
                json.put(Constant.STREAMDATA,map.get(Constant.TOTAL));
            }
                }
        );
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    @Override
    public Tip listAll(String confields, String convalues, String searchvalue) throws Exception {
        if (!StringUtils.isEmpty(searchvalue) && SearchMatch.isMatch(SearchMatch.REGEX_SEARCHVALUE,searchvalue)){
            throw new BussinessException(BizExceptionEnum.SEARCHVALUE_ERROR);
        }else {
            Map<String, String> condMap = StringUtil.StrsToMap(confields, convalues, DataSourcePo.class);
            List<DataSourcePo> dataSourcePos = dataSourceMapper.getSourceAllList(condMap, searchvalue);
            List<DataSourceDto> dtos = dataSourcePos.stream().map(p -> dataSourceEntity2Dto.entityToDto(p)).collect(Collectors.toList());
            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(), SuccessResultEnum.SUCCESS.getMessage(), dtos);
        }
    }

    /**
     * 获取分页列表
     * @param page 分页信息
     * @param confields 搜索字段名多个用,逗号分开
     * @param convalues 搜索字段值多个用,逗号分开
     * @param searchvalue 模糊匹配(默认title, 然后desc)
     * @return
     * @throws Exception
     */
    @Override
    public Tip listPage(Page page, String confields, String convalues, String searchvalue) throws Exception {
        if (!StringUtils.isEmpty(searchvalue) && SearchMatch.isMatch(SearchMatch.REGEX_SEARCHVALUE,searchvalue)){
            throw new BussinessException(BizExceptionEnum.SEARCHVALUE_ERROR);
        }else {
            Map<String, String> condMap = StringUtil.StrsToMap(confields, convalues, DataSourcePo.class);
            List<DataSourcePo> dataSourcePos = dataSourceMapper.getSourcePageList(page, condMap, searchvalue);
            List<DataSourceDto> dtos = dataSourcePos.stream().map(p -> dataSourceEntity2Dto.entityToDto(p)).collect(Collectors.toList());
            page.setRecords(dtos);
            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),page);
        }
    }

    /**
     * 添加数据源
     * @param dataSourceVo
     */
    @Override
    public Tip insertEntity(DataSourceVo dataSourceVo) throws Exception {
        DataSourcePo dp = dataSourceMapper.getByTitle(dataSourceVo.getDsTitle());
        if (null != dp){
            throw new BussinessException(BizExceptionEnum.REPEAT_ENTITY_ERROR);
        }
        DataSourcePo dataSourcePo = new DataSourcePo();
        BeanUtils.copyProperties(dataSourceVo,dataSourcePo);
        dataSourcePo.setCreateTime(new Date());
        dataSourcePo.setUpdateTime(new Date());
        boolean insert = super.insert(dataSourcePo);
        if (insert){
            return ResultUtil.result(SuccessResultEnum.ADD_SUCCESS.getCode(),SuccessResultEnum.ADD_SUCCESS.getMessage());
        }else {
            throw new BussinessException(BizExceptionEnum.SOURCE_CREATE_ERROR);
        }
    }

    /**
     * 更新操作
     * @param dataSourceVo
     * @return
     * @throws Exception
     */
    @Override
    public Tip updateEntity(DataSourceVo dataSourceVo) throws Exception {
        DataSourcePo dp = super.selectById(dataSourceVo.getId());
        if (null != dp && !dp.getDsTitle().trim().equals(dataSourceVo.getDsTitle())){
            DataSourcePo ds = dataSourceMapper.getByTitle(dataSourceVo.getDsTitle());
            if (null != ds){
                throw new BussinessException(BizExceptionEnum.REPEAT_ENTITY_ERROR);
            }
        }
        DataSourcePo dataSourcePo = new DataSourcePo();
        BeanUtils.copyProperties(dataSourceVo,dataSourcePo);
        dataSourcePo.setUpdateTime(new Date());
        boolean update = super.updateById(dataSourcePo);
        if (update){
            return ResultUtil.result(SuccessResultEnum.UPDATE_SUCCESS.getCode(),SuccessResultEnum.UPDATE_SUCCESS.getMessage());
        }else {
            throw new BussinessException(BizExceptionEnum.SOURCE_EDIT_ERROR);
        }
    }

    /**
     * 获取详情
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public Tip detail(Integer id) throws Exception {
        DataSourceDto dto = new DataSourceDto();
        DataSourcePo dataSourcePo = super.selectById(id);
        if (dataSourcePo == null){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        BeanUtils.copyProperties(dataSourcePo,dto);
        //数据源类型（1,TABLE 2,FILE 3,STREAM）
        if (dataSourcePo.getDsType().equals(DataSourceType.TABLE.getType())){
            dto.setDsType(Constant.TABLE);
        }
        if (dataSourcePo.getDsType().equals(DataSourceType.FILE.getType())){
            dto.setDsType(Constant.FILE);
        }
        if (dataSourcePo.getDsType().equals(DataSourceType.STREAM.getType())){
            dto.setDsType(Constant.STREAM);
        }
        //  引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
        if (dataSourcePo.getEngine().equals(EngineType.ORACLE.getType())){
            dto.setEngine(Constant.ORACLE);
        }
        if (dataSourcePo.getEngine().equals(EngineType.MYSQL.getType())){
            dto.setEngine(Constant.MYSQL);
        }
        if (dataSourcePo.getEngine().equals(EngineType.FTP.getType())){
            dto.setEngine(Constant.FTP);
        }
        if (dataSourcePo.getEngine().equals(EngineType.API.getType())){
            dto.setEngine(Constant.API);
        }
        dto.setUsage(dataSourceMapper.getRelateTaskNum(id));
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),dto);
    }

    /**
     * 数据源连接测试
     */
    @Override
    public Tip link(JSONObject info) {
        //引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
        logger.info(info.toJSONString());
        if (info.getInteger(Constant.ENGINE).equals(EngineType.FTP.getType())){
            FTPClient client = new FTPClient();
            try {
                //连接ftp, 登录ftp
                client.connect(info.getString(Constant.URL),info.getInteger(Constant.PORT));
                boolean login = client.login(info.getString(Constant.USERNAME), info.getString(Constant.PASSWORD));
                if (!login){
                    logger.error("ftp链接错误原因: 登录失败,请检查账号密码是否有误!");
                    throw new BussinessException(BizExceptionEnum.LINK_FAIL);
                }

                logger.info("链接ftp登录成功: " + info.toJSONString());
                return ResultUtil.result(SuccessResultEnum.LINK_SUCCESS.getCode(),SuccessResultEnum.LINK_SUCCESS.getMessage());
            } catch (IOException e) {
                logger.error("链接ftp失败: ", e);
                throw new BussinessException(BizExceptionEnum.LINK_FAIL);
            }finally {
                try {
                    client.disconnect();
                    logger.info("关闭ftp链接");
                } catch (IOException e) {
                    logger.error("ftpclient关闭失败: ",e);
                }
            }
        }else {
            Connection conn = null;
            try {
                conn = getConn(info);
                return ResultUtil.result(SuccessResultEnum.LINK_SUCCESS.getCode(),SuccessResultEnum.LINK_SUCCESS.getMessage());
            } catch (Exception e) {
                logger.error("数据库连接失败: ",e);
                throw new BussinessException(BizExceptionEnum.LINK_FAIL);
            }finally {
                try {
                    if (null != conn){
                        conn.close();
                    }
                    logger.info("关闭数据库连接");
                } catch (SQLException e) {
                    logger.info("",e);
                }
            }
        }
    }

    @Override
    public Tip delete(Integer id) throws Exception {
        DataSourcePo dataSourcePo = super.selectById(id);
        if (null == dataSourcePo){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        boolean del = super.deleteById(id);
        if (del){
            return ResultUtil.result(SuccessResultEnum.DEL_SUCCESS.getCode(),SuccessResultEnum.DEL_SUCCESS.getMessage());
        }else {
            throw new BussinessException(BizExceptionEnum.SOURCE_DEL_ERROR);
        }
    }

    /**
     * 获取数据源下的表或文件
     * @param info 数据库连接信息
     * @return
     * @throws Exception
     */
    @Override
    public Tip files(JSONObject info) throws Exception {
        //字段:url,userName,passWord,limitIp,port,parentPath
        Integer engine = info.getInteger(Constant.ENGINE);
        //引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
        if ((engine.equals(EngineType.MYSQL.getType()) || engine.equals(EngineType.ORACLE.getType()))){
            List<String> result = null;
            Connection conn = getConn(info);
            if (conn != null){
                result = getTables(conn,info.getString(Constant.USERNAME));
            }
            if (null != conn){
                conn.close();
            }
            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),result);
        }
        if (engine.equals(EngineType.FTP.getType())){
            JSONObject json = new JSONObject();
            String username = info.getString(Constant.USERNAME);
            String url = info.getString(Constant.URL);
            String password = info.getString(Constant.PASSWORD);
            int port = Integer.parseInt(info.getString(Constant.PORT));
            String parentPath = info.getString(Constant.PARENTPATH);
            List<String> dirs = ftpListFileNames(url,port,username,password,parentPath);
            json.put(Constant.FILENAME,dirs);
            json.put(Constant.PARENTPATH,parentPath);
            return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
        }
        if (engine.equals(EngineType.API.getType())){
            ResponseEntity<JSONObject> data = template.exchange(info.getString(Constant.URL), HttpMethod.GET, null, JSONObject.class);
             return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),data.getBody().get(Constant.RESULT));
        } else{
            throw new BussinessException(BizExceptionEnum.NO_OBJECT);
        }
    }

    /**
     * 预览文件信息
     */
    @Override
    public Tip preview(Integer dsId, String fileName) throws Exception {
        DataSourcePo po = dataSourceMapper.selectById(dsId);
        if (po == null){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        //字段:url,userName,passWord,limitIp,port
        JSONObject info = JSONObject.parseObject(po.getDsInfo());
        Integer engine = po.getEngine();
        //引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
        if (engine.equals(EngineType.FTP.getType())){
            String username = info.getString(Constant.USERNAME);
            String url = info.getString(Constant.URL);
            String password = info.getString(Constant.PASSWORD);
            int port = Integer.parseInt(info.getString(Constant.PORT));
            ApachePoolFTPClientImpl ftpClient = null;
            ByteArrayOutputStream out = null;
            try {
                FtpInfo ftpInfo = new FtpInfo(url,port,username,password);
                ftpClient = new ApachePoolFTPClientImpl(ftpClientPoolManager.getFtpClient(ftpInfo));
                ftpClient.setInfo(ftpInfo);
                InputStream stream = ftpClient.read(fileName);
                if(stream == null) {
                    throw new BussinessException(BizExceptionEnum.NO_OBJECT);
                }
                out = new ByteArrayOutputStream();
                int i;
                while((i = stream.read()) != -1){
                    out.write(i);
                }
                return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),out.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (null != out){
                    out.close();
                }
                if (null != ftpClient){
                    ftpClient.close();
                }
            }
        }
        throw new BussinessException(BizExceptionEnum.NO_OBJECT);
    }

    /**
     * 详细预览csv和json文件信息
     */
    @Override
    public Tip viewdetail(Integer dsId,String filePath, String fileName,Integer isHead,String sep) throws Exception {
        JSONObject json = new JSONObject();
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        JSONObject jso = new JSONObject();
        JSONArray json3 = new JSONArray();
        DataSourcePo po = dataSourceMapper.selectById(dsId);
        if (po == null){
            throw new BussinessException(BizExceptionEnum.NOT_EXSIT);
        }
        Integer engine = po.getEngine();
        JSONObject info = JSONObject.parseObject(po.getDsInfo());
        if ((engine.equals(EngineType.MYSQL.getType()) || engine.equals(EngineType.ORACLE.getType()))){
            json1.put(Constant.TYPE,Constant.TABLE);
            if (engine.equals(EngineType.ORACLE.getType())){
                json1.put(Constant.ENGINE,Constant.ORACLE);
            }
            if (engine.equals(EngineType.MYSQL.getType())){
                json1.put(Constant.ENGINE,Constant.MYSQL);
            }
            info.put(Constant.ENGINE,engine);
            try {
            Connection conn = getConn(info);
                String table;
            if (conn != null){
                if (info.getString(Constant.URL).contains(Constant.LEFTSLASH)){
                    String[] database = info.getString(Constant.URL).split(Constant.LEFTSLASH);
                    table = database[1];
                }else {
                    String[] database = info.getString(Constant.URL).split(Constant.COLON);
                    table = database[2];
                }

                jso = getCols(conn,fileName,info.getString(Constant.USERNAME),table,engine);
            }
                json.put(Constant.DSOURCE,json1);
                json.put(Constant.STRUCTURE,jso);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //字段:url,userName,passWord,limitIp,port
        if (engine.equals(EngineType.FTP.getType()) ){
            CSVReader csvReader = null;
            ApachePoolFTPClientImpl ftpClient = null;
            InputStream stream = null;
            BufferedReader br = null;
            try {
                FtpDsInfoVo ds = JSON.parseObject(info.toJSONString(), FtpDsInfoVo.class);
                FtpInfo ftpInfo = new FtpInfo(ds.getUrl(),ds.getPort(),ds.getUserName(),ds.getPassword());
                ftpClient = new ApachePoolFTPClientImpl(ftpClientPoolManager.getFtpClient(ftpInfo));
                ftpClient.setInfo(ftpInfo);
                if (StringUtils.isEmpty(fileName)){
                    FTPFile[] ftpFiles = ftpClient.listFiles(filePath);
                    if (ftpFiles.length > 0){
                        fileName = ftpFiles[0].getName();
                    }
                }
                stream = ftpClient.read(filePath+ "/" + fileName);
                if(stream == null) {
                    throw new BussinessException(BizExceptionEnum.NO_OBJECT);
                }
                if ((fileName.trim().toLowerCase().endsWith(Constant.JSCH_CSV_SIGN) || fileName.trim().toLowerCase().endsWith(Constant.JSCH_TXT_SIGN))){
                    if(StringUtils.isEmpty(sep)) {
                        throw new BussinessException(BizExceptionEnum.SEP_ISEMPTY);
                    }
                    csvReader = new CSVReader(new InputStreamReader(stream,Constant.UTF8),sep.charAt(0));
                    String[] strs = csvReader.readNext();
                    if(strs != null && strs.length > 0  && isHead == 1){
                        for(String str : strs){
                            if(!StringUtils.isEmpty(str)) {
                                JSONObject json4 = new JSONObject();
                                json4.put(Constant.FIELD,str);
                                json4.put(Constant.LENGTH,0);
                                json4.put(Constant.DESCRIPTION,"");
                                json4.put(Constant.TYPE,"");
                                json3.add(json4);
                            }
                        }
                        json2.put(Constant.COLUMN,json3);
                    }
                    if(strs != null && strs.length > 0  && isHead == 0){
//                        List<String[]> list = csvReader.readAll();
//                        list.add(strs);
                        for(int i = 0; i < strs.length; i ++){
                                JSONObject json4 = new JSONObject();
                                json4.put(Constant.FIELD, Constant.COLUMN + (i + 1));
                                json4.put(Constant.LENGTH,0);
                                json4.put(Constant.DESCRIPTION,"");
                                json4.put(Constant.TYPE,"");
                                json3.add(json4);
                            json2.put(Constant.COLUMN,json3);
                        }
                    }
                }
                if (fileName.trim().toLowerCase().endsWith(Constant.JSCH_JSON_SIGN)){
                    JSONObject jo = JSON.parseObject(stream,null);
                    Set set = jo.keySet();
                    List<String> list = new ArrayList<>();
                    list.addAll(set);
                    for (int i = 0; i < list.size(); i ++){
                        JSONObject json4 = new JSONObject();
                        json4.put(Constant.FIELD,list.get(i));
                        json4.put(Constant.LENGTH,0);
                        json4.put(Constant.DESCRIPTION,"");
                        json4.put(Constant.TYPE,"");
                        json3.add(json4);
                    }
                    json2.put(Constant.COLUMN,json3);
                }
                if (fileName.trim().toLowerCase().endsWith(Constant.JSCH_JSONS_SIGN)){
                    br=new BufferedReader(new InputStreamReader(stream));
                    String in = br.readLine();
                    JSONObject jo = JSON.parseObject(in);
                    Set set = jo.keySet();
                    List<String> list = new ArrayList<>();
                    list.addAll(set);
                    for (int i = 0; i < list.size(); i ++){
                        JSONObject json4 = new JSONObject();
                        json4.put(Constant.FIELD,list.get(i));
                        json4.put(Constant.DESCRIPTION,"");
                        json4.put(Constant.LENGTH,0);
                        json4.put(Constant.TYPE,"");
                        json3.add(json4);
                    }
                    json2.put(Constant.COLUMN,json3);
                }
            } catch (Exception e) {
                logger.error("{}",e);
            } finally {
                try {
                    if (null != csvReader){
                        csvReader.close();
                    }
                    if (stream != null){
                        stream.close();
                    }
                    if (null != br){
                        br.close();
                    }
                } catch (IOException e) {
                    logger.error("{}",e);
                }
            }
            json1.put(Constant.TYPE,Constant.FILE);
            json1.put(Constant.ENGINE,Constant.FTP);
            json.put(Constant.DSOURCE,json1);
            json.put(Constant.STRUCTURE,json2);
        }
        return ResultUtil.result(SuccessResultEnum.SUCCESS.getCode(),SuccessResultEnum.SUCCESS.getMessage(),json);
    }

    //获取表名
    public  List<String> getTables(Connection conn,String userName){
        List<String> result = new ArrayList<>();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rsTables = meta.getTables(null, userName , null, new String[] { Constant.TABLE });
            while(rsTables.next()) {
                String tableName = rsTables.getString(Constant.TABLE_NAME);
                result.add(tableName);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //获取表中字段名
    public  JSONObject getCols(Connection conn, String tableName,String userName,String database,Integer engine) throws Exception {
        JSONObject tableInfo = new JSONObject();
        ResultSet colRet = null;
        if ((engine.equals(EngineType.MYSQL.getType()))){
            Statement statement = conn.createStatement();
            String sql = "SELECT * FROM information_schema.COLUMNS WHERE table_schema = '"+database+"' AND table_name = '"+tableName+"' order by ORDINAL_POSITION";
             colRet = statement.executeQuery(sql);
        }
        if((engine.equals(EngineType.ORACLE.getType()))) {
            DatabaseMetaData dbMetData;
            dbMetData = conn.getMetaData();
             colRet = dbMetData.getColumns(conn.getCatalog(), userName, tableName, "%");
        }
            JSONArray json1 = new JSONArray();
            while (colRet.next()) {
                JSONObject json = new JSONObject();
                String columnType = "";
                String datasize = "";
                String description = "";
                String columnName = colRet.getString(Constant.COLUMN_NAME);
                if ((engine.equals(EngineType.MYSQL.getType()))){
                    String colType = colRet.getString(Constant.COLUMN_TYPE);
                    columnType = getColumnType(colType);
                    datasize = getColumnLength(colType);
                    description = colRet.getString(Constant.COLUMN_COMMENT);
                }
                if ((engine.equals(EngineType.ORACLE.getType()))){
                    columnType = colRet.getString(Constant.TYPE_NAME);
                    datasize = colRet.getString(Constant.COLUMN_SIZE);
                    description = colRet.getString(Constant.REMARKS);
                }
                if (columnType.toLowerCase().contains("int unsigned")){
                    columnType = Constant.INT;
                }
                if (columnType.toLowerCase().contains(Constant.DATE_TIME) || columnType.toLowerCase().contains(Constant.DATE)
                        || columnType.toLowerCase().contains(Constant.TEXT)){
                    datasize = Constant.NUM_STR_0;
                }
                json.put(Constant.FIELD,columnName);
                json.put(Constant.TYPE,columnType);
                json.put(Constant.LENGTH,datasize);
                json.put(Constant.DESCRIPTION,description);
                json1.add(json);
            }
            tableInfo.put(Constant.COLUMN,json1);
        return tableInfo;
    }

    //获取ftp文件名列表
    public List<String> ftpListFileNames(String host, int port, String username, String password, String path) {
        List<String> list = new ArrayList<>();
        ApachePoolFTPClientImpl ftpClient = null;
        try {
            FtpInfo ftpInfo = new FtpInfo(host,port,username,password);
            ftpClient = new ApachePoolFTPClientImpl(ftpClientPoolManager.getFtpClient(ftpInfo));
            ftpClient.setInfo(ftpInfo);
            FTPFile[] ftpFiles = ftpClient.listFiles(path);
            for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
                FTPFile file = ftpFiles[i];
                if (file.isFile()) {
                    list.add(file.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != ftpClient){
                try {
                    ftpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    //获取conn连接
    public Connection getConn(JSONObject info) throws BussinessException {
        DruidDataSource dataSource = null;
        DruidPooledConnection connection = null;
        try {
            if (connectionMap.containsKey(info)){
                dataSource = connectionMap.get(info);
            }else {
                //引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
                dataSource = new DruidDataSource();
                String driver = "";
                String url = "";
                //数据源类型（1：MYSQL  2：FILE 3：ORACLE 4：API）
                if (info.getInteger(Constant.ENGINE).equals(EngineType.MYSQL.getType())) {
                    driver = "com.mysql.jdbc.Driver";
                    url = "jdbc:mysql://" + info.getString(Constant.URL) + "?useSSL=false&useUnicode=true&characterEncoding=utf8&failOverReadOnly=false";
                }
                if (info.getInteger(Constant.ENGINE).equals(EngineType.ORACLE.getType())) {
                    driver = "oracle.jdbc.driver.OracleDriver";
                    url = "jdbc:oracle:thin:@" + info.getString(Constant.URL);
                }
                String user = info.getString(Constant.USERNAME);
                String password = info.getString(Constant.PASSWORD);

                dataSource.setDriverClassName(driver);
                dataSource.setUrl(url);
                dataSource.setUsername(user);
                dataSource.setPassword(password);
                //初始化时建立物理连接的个数
                dataSource.setInitialSize(5);
                //最小连接池数量
                dataSource.setMinIdle(1);
                //最大连接池数量
                dataSource.setMaxActive(30);
                //是否缓存preparedStatement,比如说oracle,mysql5.5以下的版本中没有PSCache功能，建议关闭掉5.5及以上版本有PSCache，建议开启。
                dataSource.setPoolPreparedStatements(true);
                dataSource.setRemoveAbandoned(false);
                //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于
                //timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
                dataSource.setTestWhileIdle(true);
                dataSource.init();
                connectionMap.put(info,dataSource);
            }
            connection = dataSource.getConnection();
            return connection;
        }catch (Exception e){
            throw new BussinessException(BizExceptionEnum.LINK_FAIL);
        }
    }

    public static String getColumnLength(String columnTypeStr) {
        columnTypeStr = columnTypeStr.replaceAll(Constant.UNSIGNED,"").trim();
        String columnLength = "";
        if (StringUtils.isEmpty(columnTypeStr)) {
            return "";
        }
        if (columnTypeStr.indexOf("(") > -1) {
            columnLength = columnTypeStr.substring(columnTypeStr.indexOf("(") + 1, columnTypeStr.length() - 1);
        } else {
            columnLength = "";
        }

        return columnLength;
    }

    public static String getColumnType(String columnTypeStr) {
        columnTypeStr = columnTypeStr.replaceAll(Constant.UNSIGNED,"").trim();
        String columnType = "";
        if (StringUtils.isEmpty(columnTypeStr)) {
            return "";
        }
        if (columnTypeStr.indexOf("(") > -1) {
            columnType = columnTypeStr.substring(0, columnTypeStr.indexOf("("));
        } else {
            columnType = columnTypeStr;
        }

        return columnType;
    }

}
