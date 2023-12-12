package com.jingxin.wxshop;

import com.jingxin.wxshop.service.AuthService;
import com.jingxin.wxshop.service.TelVerificationService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final TelVerificationService telVerificationService;
    private final SecurityManager securityManager;
    private static final Logger logger = LogManager.getLogger("AuthController");

    @Autowired
    public AuthController(AuthService authService, TelVerificationService telVerificationService, SecurityManager securityManager) {
        this.authService = authService;
        this.telVerificationService = telVerificationService;
        this.securityManager = securityManager;
    }

    @PostConstruct
    private void initStaticSecurityManager() {
        SecurityUtils.setSecurityManager(securityManager);
    }

    @PostMapping("/code")
    public void code(@RequestBody TelAndCode code, HttpServletResponse response) {
        if (!telVerificationService.verifyTelParameter(code)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        authService.sendVerificationCode(code.getTel());
        ResponseEntity.status(HttpStatus.OK.value());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody TelAndCode telAndCode, HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token =
                    new UsernamePasswordToken(telAndCode.getTel(), telAndCode.getCode());
            token.setRememberMe(true);
            try {
                logger.info("login checker");
                subject.login(token);
            } catch (Exception uae) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public void loginStatus(){
       logger.info(SecurityUtils.getSubject().getPrincipal());
    }
    public static class TelAndCode {
        private String tel;
        private String code;

        public TelAndCode(String tel, String code) {
            this.tel = tel;
            this.code = code;
        }

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
