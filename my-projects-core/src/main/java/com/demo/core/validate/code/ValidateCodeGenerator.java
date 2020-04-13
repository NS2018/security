package com.demo.core.validate.code;

import com.demo.core.validate.code.dto.ValidateCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.ServletWebRequest;

public interface ValidateCodeGenerator {

    ValidateCode generate(ServletWebRequest request);
}
