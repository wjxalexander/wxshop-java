package com.jingxin.wxshop.service;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroRealmService extends AuthorizingRealm {
    private final VerificationCodeCheckService verificationCodeCheckService;

    @Autowired
    public ShiroRealmService(VerificationCodeCheckService verificationCodeCheckService) {
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.setCredentialsMatcher((authenticationToken, authenticationInfo) -> new String((char[]) authenticationToken.getCredentials()).equals(authenticationInfo.getCredentials()));
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 看看这个人是不是你说的自己， 货是否对板， 人是否对的上身份证照片
        String tel = (String) authenticationToken.getPrincipal();
        String correctCode = this.verificationCodeCheckService.getCorrectCode(tel);
        return new SimpleAuthenticationInfo(tel, correctCode, getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 有没有访问权限 权限控制
        return null;
    }

}
