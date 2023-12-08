package com.jingxin.wxshop;

import com.jingxin.wxshop.response.VerificationCodeResponse;
import com.jingxin.wxshop.service.AuthService;
import com.jingxin.wxshop.service.TelVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
  private final AuthService authService;
  private final TelVerificationService telVerificationService;

  @Autowired
  public AuthController(AuthService authService, TelVerificationService telVerificationService) {
    this.authService = authService;
    this.telVerificationService = telVerificationService;
  }

  @PostMapping("/code")
  public void code(@RequestBody TelAndCode code, HttpServletResponse response) {
    if (!telVerificationService.verifyTelParameter(code)) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    String authCode = authService.sendVerificationCode(code.getTel());
    ResponseEntity.status(HttpStatus.OK.value());
  }

  @PostMapping("/login")
  public void login(@RequestBody TelAndCode telAndCode, HttpServletResponse response) {
    Subject subject = SecurityUtils.getSubject();
    if (!subject.isAuthenticated()) {
      UsernamePasswordToken token =
          new UsernamePasswordToken(telAndCode.getTel(), telAndCode.getCode());
      token.setRememberMe(true);
      try {
        subject.login(token);
      } catch (Exception uae) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
      }
    }
  }

  public static class TelAndCode {
    private String tel;
    private String code;

    public String getTel() {
      return tel;
    }

    public void setTel(String tel) {
      this.tel = tel;
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }
  }
}
