package com.jingxin.wxshop.service;

public interface SmsCodeService {
    /**
     *
     * @param telephoneNumber
     * @return code
     */
    String sendSmsCode(String telephoneNumber);
}
