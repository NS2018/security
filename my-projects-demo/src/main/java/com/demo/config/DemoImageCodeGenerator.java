package com.demo.config;

import com.demo.core.validate.code.dto.ImageCode;
import com.demo.core.validate.code.ValidateCodeGenerator;
import org.springframework.web.context.request.ServletWebRequest;


//@Component("imageCodeGenerator")
public class DemoImageCodeGenerator implements ValidateCodeGenerator {
    @Override
    public ImageCode generate(ServletWebRequest request) {
        System.out.println("高级用法图形验证码");
        return null;
    }
}
