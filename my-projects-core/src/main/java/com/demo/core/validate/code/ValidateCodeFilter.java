package com.demo.core.validate.code;

import com.demo.core.properties.SecurityConstants;
import com.demo.core.properties.SecurityProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    /**
     * 系统 配置参数
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 存放所有需要校验验证码的url
     */
    private Map<String,ValidateCodeType> urlMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        urlMap.put(SecurityConstants.DEFAULT_SIGN_IN_PROCESSING_URL_FORM,ValidateCodeType.IMAGE);
        addUrlToMap(securityProperties.getCode().getImage().getUrl(),ValidateCodeType.IMAGE);

        urlMap.put(SecurityConstants.DEFAULT_SIGN_IN_PROCESSING_URL_MOBILE,ValidateCodeType.SMS);
        addUrlToMap(securityProperties.getCode().getImage().getUrl(),ValidateCodeType.SMS);
    }

    /**
     * 将系统中配置的需要校验验证码的url根据校验的类型放入map
     *
     * @param url
     * @param type
     */
    private void addUrlToMap(String url, ValidateCodeType type) {
        if(StringUtils.isNotBlank(url)){
            String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(url, ",");
            for (String validateUrl : urls) {
                urlMap.put(validateUrl,type);
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ValidateCodeType type = getValidateCodeType(request);
        if(type != null){
            logger.info("校验请求(" + request.getRequestURI() + ")中的验证码,验证码类型" + type);
            try {
                validateCodeProcessorHolder.findValidateCodeProcessor(type);
            }catch (ValidateCodeException exception){
                authenticationFailureHandler.onAuthenticationFailure(request,response,exception);
                return;
            }
        }

        filterChain.doFilter(request,response);
    }

    /**
     * 获取校验码的类型，如果当前请求不需要校验，返回null
     * @param request
     * @return
     */
    private ValidateCodeType getValidateCodeType(HttpServletRequest request) {
        ValidateCodeType result = null;
        if(!StringUtils.equalsIgnoreCase(request.getMethod(),"get")){
            Set<String> urls = urlMap.keySet();
            for (String url : urls) {
                if(antPathMatcher.match(url,request.getRequestURI())){
                    result = urlMap.get(url);
                    break;
                }
            }
        }
        return result;
    }


}
