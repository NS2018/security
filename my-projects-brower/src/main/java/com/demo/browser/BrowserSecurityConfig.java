package com.demo.browser;

import com.demo.browser.authentication.MyAuthenticationFailureHandler;
import com.demo.browser.authentication.MyAuthenticationSuccessHandler;
import com.demo.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.demo.core.authentication.mobile.SmsCodeFilter;
import com.demo.core.properties.SecurityProperties;
import com.demo.core.validate.code.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

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
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MyAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Bean
    @ConditionalOnMissingBean(MyUserDetailService.class)
    public UserDetailsService userDetailsService(){
        return new MyUserDetailService();
    }

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

        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        validateCodeFilter.setSecurityProperties(securityProperties);
        validateCodeFilter.afterPropertiesSet();

        SmsCodeFilter smsCodeFilter = new SmsCodeFilter();
        smsCodeFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        smsCodeFilter.setSecurityProperties(securityProperties);
        smsCodeFilter.afterPropertiesSet();
        http
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .loginPage("/authentication/require")
                .loginProcessingUrl("/authentication/form")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .rememberMe()
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeInSeconds())
                .tokenRepository(persistentTokenRepository())
                .and()
                .authorizeRequests()
                .antMatchers("/authentication/require",securityProperties.getBrowser().getLoginPage(),
                        "/code/*").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable()
                .apply(smsCodeAuthenticationSecurityConfig);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .and()
                .jdbcAuthentication()
                .passwordEncoder(passwordEncoder())
                .dataSource(dataSource);
    }
}
