package com.demo.browser;

import com.demo.browser.session.MyExpiredSessionStrategy;
import com.demo.core.authentication.FormAuthenticationConfig;
import com.demo.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.demo.core.properties.SecurityConstants;
import com.demo.core.properties.SecurityProperties;
import com.demo.core.validate.code.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * 原理：过滤器链
 *
 * 核心：
 * 可配置
 * UsernamePasswordAuthenticationFilter 表单
 *      BasicAuthenticationFilter basic
 *
 * 不可配置
 *      ExceptionTranslationFilter (异常)
 *      FilterSecurityInterceptor(最后一环) 能不能访问对应接口服务
 *
 * 基本原理：过滤器链
 * securityContextPersistenceFilter
 * UsernamePasswordAuthenticationFilter
 * BasicAuthenticationFilter
 * RememberMeAuthenticationFilter
 * ExceptionTranslateFilter
 * filterSecurityInterceptor
 *
 * 用户名密码认证方式
 * UsernamePasswordAuthenticationFIlter
 *      UsernamePasswordAuthenticationToken
 * AuthenticationManager
 * AuthenticationProvider
 * UserDetailService
 * UserDetails
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    private FormAuthenticationConfig formAuthenticationConfig;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;

    @Autowired
    private SpringSocialConfigurer mySpringSocialConfig;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        formAuthenticationConfig.configure(http);

        http.apply(validateCodeSecurityConfig)
                .and()
            .apply(smsCodeAuthenticationSecurityConfig)
                .and()
            .apply(mySpringSocialConfig)
                .and()
            .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeInSeconds())
                .userDetailsService(userDetailsService)
                .and()
            .sessionManagement()
                .invalidSessionUrl("/session/invalid")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .expiredSessionStrategy(new MyExpiredSessionStrategy())
                .and()
                .and()
            .authorizeRequests()
                .antMatchers(
                        SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
                        SecurityConstants.DEFAULT_SIGN_IN_PROCESSING_URL_MOBILE,
                        securityProperties.getBrowser().getLoginPage(),
                        SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*",
                        securityProperties.getBrowser().getSignUpUrl(),
                        "/user/regist","/session/invalid").permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .csrf().disable();
    }
}
