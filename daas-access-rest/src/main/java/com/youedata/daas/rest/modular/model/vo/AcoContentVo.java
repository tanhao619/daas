package com.youedata.daas.rest.modular.model.vo;

import com.youedata.daas.rest.modular.model.ApiDesc;
import com.youedata.daas.rest.modular.model.FtpDesc;

/**
 * Created by chengtao on 2017/10/23 0018.
 */
public class AcoContentVo {
    private FtpDesc ftpDesc;
    private ApiDesc apiDesc;

    public FtpDesc getFtpDesc() {
        return ftpDesc;
    }

    public void setFtpDesc(FtpDesc ftpDesc) {
        this.ftpDesc = ftpDesc;
    }

    public ApiDesc getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(ApiDesc apiDesc) {
        this.apiDesc = apiDesc;
    }
}
