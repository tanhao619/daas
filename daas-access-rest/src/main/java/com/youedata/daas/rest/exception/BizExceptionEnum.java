package com.youedata.daas.rest.exception;

/**
 * 所有业务异常的枚举
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午5:04:51
 */
public enum BizExceptionEnum {

    SUCCESS(200,"操作成功"),
    /**
     * token异常
     */
    TOKEN_EXPIRED(700, "token过期"),
    TOKEN_ERROR(700, "token验证失败"),

    /**
     * hdfs异常
     */
    HDFS_READ_SPECIFIED_SIZE_ERROR(600,"按指定大小读取hdfs文件错误"),
    HDFS_FILE_TYPE_NOT_SUPPORT_PREVIEW(600,"文件类型不可预览"),
    HDFS_FILE_DOWNLOAD_ERROR(600,"文件下载错误"),
    HDFS_FILE_CLOSE_ERROR(600,"关闭输入流错误"),

    /**
     * 签名异常
     */
    SIGN_ERROR(700, "签名验证失败"),

    /**
     * 其他
     */
    NO_OBJECT(400, "未找到数据"),
    LINK_FAIL(509,"连接失败"),
    IS_DIRECTORY(510,"这是一个文件夹"),
    /**
     * 其他
     */
    USER_ALREADY_REG(401,"该用户已经注册"),
    NO_THIS_USER(400,"没有此用户"),
    API_RESULT_CODE_MSG_MUST_NOT_EMPTY(500, "参数不能为空"),
    TWO_PWD_NOT_MATCH(405, "两次输入密码不一致"),
    AUTH_REQUEST_ERROR(400, "账号密码错误"),
    REQUEST_INVALIDATE(400,"请求数据格式不正确"),
    OLD_PWD_NOT_RIGHT(402, "原密码不正确"),
    NULL_ENTITY_ERROR(500, "实体类为空"),
    CONN_ERROR(506, "链接失败"),
    SYS_ERROR(500, "系统错误"),
    NOT_EXSIT(500, "该实体类不存在"),
    NOT_EMPTY(511, "传入数据不能为空"),
    DATASET_NO_FILE(400, "数据集下没有文件"),

    /*
    业务逻辑状态码
     */
    REPEAT_ENTITY_ERROR(501, "标题重复"),
    PATAM_ERROR(502, "搜索条件有误"),
    TOO_LONG(503, "标识超过了限定长度"),
    PARAM_ERROR(504, "传入参数有误"),
    PARAM_USER_NOT_EXIST(507, "传入用户ID不存在"),

    /**
     * 其他
     */
    SEARCHVALUE_ERROR(500, "模糊查询条件存在非法字符"),
    HDFS_CREATE_ERROR(501,"HDFS创建文件错误"),
    HDFS_DELETE_ERROR(502,"HDFS删除文件错误"),
    HDFS_OPEN_ERROR(503,"HDFS打开文件错误"),
    HDFS_RENAME_ERROR(504,"HDFS重命名文件错误"),
    HDFS_CONNECT_ERROR(505,"HDFS连接错误"),
    HDFS_DISCONNECT_ERROR(506,"HDFS端口连接错误"),

    /**
     * 其他
     */
    FTP_CONNECT_ERROR(601,"ftp连接错误"),
    FTP_DISCONNECT_ERROR(602,"ftp断开连接错误"),
    FTP_PENDING_ERROR(603,"ftp处理completePendingCommand错误"),
    FTP_MKDIR_ERROR(604,"ftp创建目录错误"),
    FTP_REPLYCODE_ERROR(605,"ftp得到replycode错误"),
    FTP_PUT_ERROR(606,"ftp上传文件错误"),
    FTP_GET_ERROR(607,"ftp下载文件错误"),
    FTP_DELETE_ERROR(608,"ftp删除文件错误"),
    FTP_LIST_ERROR(609,"ftp获取文件列表错误"),
    FTP_COMMAND_ERROR(610,"ftp发生命令错误"),
    FTP_SIZE_ERROR(611,"ftp得到文件大小错误"),
    FTP_DS_ERROR(612,"ftp获取数据源错误"),
    FTP_RULE_ERROR(613,"ftp目标介质未定义"),


    /**
     * 任务相关
     */
    TASK_CREATE_ERROR(701,"任务创建失败"),
    TASK_EDIT_ERROR(702,"任务修改错误"),
    TASK_START_OR_STOP_ERROR(703,"任务启停错误"),
    TASK_TITLE_REPEAT(704,"任务名称重复"),
    TASK_REQUEST_RAPRAMS_ERROR(705,"请求参数有误"),
    TASK_START_ERROR(706,"任务启动失败"),
    TASK_STOP_ERROR(707,"任务暂停失败"),
    TASK_DEL_ERROR(708,"任务删除失败"),
    TASK_WRITE_ERROR(709,"数据写入错误"),
    TASK_FILE_FORMAT_ERROR(710,"数据大小格式不正确"),
    TASK_RES_CREATE_ERROR(711,"数据集创建失败"),
    TASK_RELATION_CREATE_ERROR(712,"血缘关系修改失败"),
    TASK_KINS_UPDATE_ERROR(713,"修改数据集上下游血缘信息失败"),
    TASK_DATA_INFO_ERROR(714,"获取数据单元信息失败"),
    TASK_DN_CREATE_ERROR(715,"数据节点创建失败"),
    TASK_DU_CREATE_ERROR(716,"数据单元创建失败"),
    TASK_DU_PUT_ERROR(717,"数据上传失败"),
    TASK_RES_DETAIL_ERROR(718,"获取数据集详情失败"),
    TASK_RES_TYPE_ERROR(719,"数据集不匹配"),
    TASK_RES_HAVE_ERROR(720,"数据集不存在"),
    TASK_TABLE_CREATE_ERROR(721,"建表失败"),
    TASK_QUARTZ_FIRE_ERROR(722,"任务时间有误,将不会被执行"),
    TASK_MEDIUM_TABLE_ERROR(723,"目标表不存在"),
    TASK_DS_RES_REPEAT(724, "相同的数据源和数据集任务已存在!"),
    TASK_RES_SINGLE(725, "请选择单表数据集!"),
    /**
     * 数据源相关
     */
    SOURCE_CREATE_ERROR(801,"数据源创建失败"),
    SOURCE_EDIT_ERROR(802,"数据源修改错误"),
    SOURCE_DEL_ERROR(803,"数据源删除失败"),
    SEP_ISEMPTY(804,"CSV文件分隔符未定义"),
    /**
     * 文件元数据
     */
    FILE_METADATA_CREATE_ERROR(801,"文件元数据创建错误"),
    /**
     * 其他
     */
    WRITE_ERROR(500,"渲染界面错误"),

    /**
     * 文件上传
     */
    FILE_READING_ERROR(400,"FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400,"FILE_NOT_FOUND!"),

    /**
     * 错误的请求
     */
    REQUEST_NULL(400, "请求有错误"),
    DIR_FILE_ISEMPTY(805, "文件或目录不存在"),
    SERVER_ERROR(500, "服务器异常"),

    /**
     *  任务调度
     */
    TASK_DB_DISPATCH_ERROR(600, "读取数据库失败"),
    TASK_SOURCE_TYPE_NOT_FOUND(601,"数据源类型未找到"),
    ;
    BizExceptionEnum(int code, String message) {
        this.friendlyCode = code;
        this.friendlyMsg = message;
    }


    private int friendlyCode;

    private String friendlyMsg;

    public int getCode() {
        return friendlyCode;
    }

    public void setCode(int code) {
        this.friendlyCode = code;
    }

    public String getMessage() {
        return friendlyMsg;
    }

    public void setMessage(String message) {
        this.friendlyMsg = message;
    }

}
