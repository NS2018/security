package com.demo.core.validate.code.service.impl;

import com.demo.core.validate.code.service.SmsCodeSender;

public class DefaultSmsCodeSender implements SmsCodeSender {
    @Override
    public void send(String mobile, String code) {
        System.out.println("向"+mobile+"验证码："+code);
    }
}
