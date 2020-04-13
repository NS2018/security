package com.demo.core.validate.code;

import org.springframework.web.context.request.ServletWebRequest;

public interface ValidateCodeProcessor {

    /**
     * 验证码session中的前缀
     */
    String SESSION_KEY_PREFIX = "SESSION_KEY_IMAGE_CODE_";

    /**
     * 创建校验码
     * @param request
     * @throws Exception
     */
    void create(ServletWebRequest request) throws Exception;
}
