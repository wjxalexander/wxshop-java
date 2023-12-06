package com.jingxin.wxshop.config;

import com.jingxin.wxshop.service.ShiroRealmService;
import com.jingxin.wxshop.service.VerificationCodeCheckService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.ShiroRequestMappingConfig;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import({ShiroBeanConfiguration.class,
        ShiroAnnotationProcessorConfiguration.class,
        ShiroWebConfiguration.class,
        ShiroWebFilterConfiguration.class,
        ShiroRequestMappingConfig.class})
public class ShiroConfig {
    public static final String ANONYMOUS = "anon";
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> pattern = new HashMap<>();
        shiroFilterFactoryBean.setLoginUrl("/api/login");
        pattern.put("/api/code", ANONYMOUS);
        pattern.put("/api/login", ANONYMOUS);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);

        return shiroFilterFactoryBean;
    }
    @Bean
    public ShiroRealmService shiroRealm(VerificationCodeCheckService verificationCodeCheckService) {
        return new ShiroRealmService(verificationCodeCheckService);
    }
    @Bean
    public DefaultWebSecurityManager customSecurityManager(VerificationCodeCheckService verificationCodeCheckService) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm(verificationCodeCheckService));
        securityManager.setRememberMeManager(rememberMeManager());
        SecurityUtils.setSecurityManager(securityManager); // Set the SecurityManager as a VM static singleton;
        return securityManager;
    }
    @Bean
    public ShiroRealmService myRealm(VerificationCodeCheckService verificationCodeCheckService) {
        ShiroRealmService realm = new ShiroRealmService(verificationCodeCheckService);
        return realm;
    }

    @Bean("name=myRememberMeCookie")
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("MY_REMEMBER_ME_COOKIE");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(2592000); // 30 days in seconds
        // Customize other cookie settings if needed
        return cookie;
    }

    @Bean("name=rememberMeManager")
    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCookie(rememberMeCookie());
        // Customize other settings if needed
        return rememberMeManager;
    }

}
