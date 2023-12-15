package com.jingxin.wxshop.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class AuthService  {
    private final UserService userService;
    private final VerificationCodeCheckService verificationCodeCheckService;
    private final SmsCodeService smsCodeService;
    @Autowired
    public AuthService(UserService userService, VerificationCodeCheckService verificationCodeCheckService, SmsCodeService smsCodeService) {
        this.userService = userService;
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.smsCodeService = smsCodeService;
    }

    public String sendVerificationCode(String tel) {
      userService.createUserIfNotExist(tel);
      String code = smsCodeService.sendSmsCode(tel);
      verificationCodeCheckService.addCode(tel, code);
      return code;
    }

}
