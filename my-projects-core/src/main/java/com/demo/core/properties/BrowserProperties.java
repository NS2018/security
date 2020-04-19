package com.demo.core.properties;

public class BrowserProperties {

    private String loginPage = "/signIn.html";
    private String signUpUrl = "/signUp.html";
    private String logoutUrl;


    //默认返回类型
    private LoginType loginType = LoginType.JSON;

    private int rememberMeInSeconds = 3600;

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public int getRememberMeInSeconds() {
        return rememberMeInSeconds;
    }

    public void setRememberMeInSeconds(int rememberMeInSeconds) {
        this.rememberMeInSeconds = rememberMeInSeconds;
    }

    public String getSignUpUrl() {
        return signUpUrl;
    }

    public void setSignUpUrl(String signUpUrl) {
        this.signUpUrl = signUpUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
}
