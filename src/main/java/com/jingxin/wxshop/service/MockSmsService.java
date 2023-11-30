package com.jingxin.wxshop.service;

import org.springframework.stereotype.Service;

@Service
public class MockSmsService implements SmsCodeService{
    private static String MOCK_CODE = "123456";
    @Override
    public String sendSmsCode(String telephoneNumber) {
        // 1. 限流
        // 2. 暴力破解怎么办
        return MOCK_CODE;
    }
}
