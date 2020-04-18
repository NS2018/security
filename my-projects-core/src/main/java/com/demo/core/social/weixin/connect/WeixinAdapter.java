package com.demo.core.social.weixin.connect;

import com.demo.core.social.weixin.api.Weixin;
import com.demo.core.social.weixin.api.WeixinUserInfo;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

public class WeixinAdapter implements ApiAdapter<Weixin> {

    private String openId;

    public WeixinAdapter() {}

    public WeixinAdapter(String openId){
        this.openId = openId;
    }

    @Override
    public boolean test(Weixin weixin) {
        return true;
    }

    @Override
    public void setConnectionValues(Weixin weixin, ConnectionValues connectionValues) {
        WeixinUserInfo profile = weixin.getUserInfo(openId);
        connectionValues.setProviderUserId(profile.getOpenid());
        connectionValues.setDisplayName(profile.getNickname());
        connectionValues.setImageUrl(profile.getHeadimgurl());
    }

    @Override
    public UserProfile fetchUserProfile(Weixin weixin) {
        return null;
    }

    @Override
    public void updateStatus(Weixin weixin, String s) {

    }
}
