package com.xutong.uploadlocation;

/**
 * Created by Administrator on 2015/11/2.
 */
public class UserInfo {


    /**
     * loginUser : 888888
     * pw : 888888
     * saveUid : true
     * saveUpwd : true
     */

    private String loginUser;
    private String pw;
    private String saveUid;
    private String saveUpwd;

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setSaveUid(String saveUid) {
        this.saveUid = saveUid;
    }

    public void setSaveUpwd(String saveUpwd) {
        this.saveUpwd = saveUpwd;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public String getPw() {
        return pw;
    }

    public String getSaveUid() {
        return saveUid;
    }

    public String getSaveUpwd() {
        return saveUpwd;
    }
}
