package com.jingxin.wxshop.config;

import com.jingxin.wxshop.service.ShiroRealm;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration

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
    public DefaultWebSecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        securityManager.setSessionManager(rememberMeSessionManager());
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        return securityManager;
    }

    @Bean("name=myRememberMeCookie")
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("sid");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(2592000); // 30 days in seconds
        // Customize other cookie settings if needed
        return cookie;
    }

    @Bean
    public SessionManager rememberMeSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(rememberMeCookie());    // Customize other settings if needed
        return sessionManager;
    }
}
