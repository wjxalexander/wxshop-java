package com.jingxin.wxshop.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Service
public class UserLoginInterceptor implements HandlerInterceptor {
    private UserService userService;
    private static final Logger logger = LogManager.getLogger(UserLoginInterceptor.class);
    @Autowired
    public UserLoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//            return HandlerInterceptor.super.preHandle(request, response, handler);
        Object tel = SecurityUtils.getSubject().getPrincipal();
        if (tel != null) {
            userService.getUserByTel(tel.toString()).ifPresent(UserContext::setCurrentUser);
        }
        logger.info("prehandle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//            HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        //!important: 线程会被复用！！！防治线程服用的时候串号！
        UserContext.setCurrentUser(null);
        logger.info("post");
    }
}
