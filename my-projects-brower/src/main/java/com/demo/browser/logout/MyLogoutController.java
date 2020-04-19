package com.demo.browser.logout;

import com.demo.browser.support.SimpleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyLogoutController implements LogoutSuccessHandler {

    private String logoutUrl = "";

    private ObjectMapper objectMapper = new ObjectMapper();

    public MyLogoutController(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if(StringUtils.isBlank(logoutUrl)){

            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse("退出成功")));
        }else {
            httpServletResponse.sendRedirect(logoutUrl);
        }
    }
}
