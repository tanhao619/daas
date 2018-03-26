package com.youedata.daas.rest.common.filter.userFilter;

public class User {
    private String accountId; // 用户ID
    private String accountName; // 用户名称
    private Boolean bAdmin;
    private String tenantId;    // 租户ID
    private String accessToken;    // token

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Boolean getbAdmin() {
        return bAdmin;
    }

    public void setbAdmin(Boolean bAdmin) {
        this.bAdmin = bAdmin;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "accountId='" + accountId + '\'' +
                ", accountName='" + accountName + '\'' +
                ", bAdmin=" + bAdmin +
                ", tenantId='" + tenantId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
