package com.demo.core.social.qq.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.io.IOException;

/**
 * 获取用户信息
 */
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

    private String appId;
    private String openId;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?" +
            "access_token=%s";
    private static final String URL_GET_USER_INFO = "https://graph.qq.com/user/get_user_info?" +
            "oauth_consumer_key=%s&" +
            "openid=%s";

    public QQImpl(String accessToken,String appId){
        //自动将查询参数挂到url上
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.appId = appId;
        String url = String.format(URL_GET_OPENID, accessToken);
        String result = getRestTemplate().getForObject(url, String.class);

        System.out.println(result);

        this.openId = StringUtils.substringBetween(result,"\"openid\":","}");
    }

    @Override
    public QQUserInfo getUserInfo(){

        String url = String.format(URL_GET_USER_INFO,appId,openId);
        String result = getRestTemplate().getForObject(url,String.class);

        System.out.println(result);
        try {
            return objectMapper.readValue(result, QQUserInfo.class);
        } catch (IOException e) {
            throw new RuntimeException("获取用户信息失败");
        }
    }

}