package com.demo.core.validate.code;

import com.demo.core.properties.SecurityConstants;
import com.demo.core.properties.SecurityProperties;
import com.demo.core.validate.code.dto.ImageCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    private AuthenticationFailureHandler authenticationFailureHandler;
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
    private Set<String> urls = new HashSet<>();
    private SecurityProperties securityProperties;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void afterPropertiesSet() throws ServletException {
        String url = securityProperties.getCode().getImage().getUrl();
        String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(url, ",");
        for (String validateUrl : configUrls) {
            urls.add(validateUrl);
        }

        urls.add("/authentication/form");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean matched = false;
        for (String url : urls) {
            String uri = request.getRequestURI();
            if(antPathMatcher.match(url,uri)){
                matched = true;
            }
        }
        if(matched){
            try {
                validate(new ServletWebRequest(request));
            }catch (ValidateCodeException e){
                authenticationFailureHandler.onAuthenticationFailure(request,response,e);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private void validate(ServletWebRequest request) throws ServletRequestBindingException {

        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request, "IMAGE");

        if(codeInSession == null){
            throw new ValidateCodeException("验证码不存在");
        }

        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), "imageCode");

        if(StringUtils.isBlank(codeInRequest)){
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if(codeInSession.isExpired()){
            sessionStrategy.removeAttribute(request,SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_IMAGE);
            throw new ValidateCodeException("验证码已过期");
        }

        if(!StringUtils.equals(codeInSession.getCode(),codeInRequest)){
            throw  new ValidateCodeException("验证码不匹配");
        }

        sessionStrategy.removeAttribute(request, SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_IMAGE);


    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public SessionStrategy getSessionStrategy() {
        return sessionStrategy;
    }

    public void setSessionStrategy(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
