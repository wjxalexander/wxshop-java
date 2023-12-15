package com.jingxin.wxshop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jingxin.wxshop.generate.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class LoginStatusResponse {
    @JsonProperty("login")
    private boolean login;
    @JsonProperty("user")
    private User user;

    public boolean isLogin() {
        return login;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public User getUser() {
        return user;
    }
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public LoginStatusResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    // Default constructor
    public LoginStatusResponse() {
    }

    public static LoginStatusResponse notLogin(){
        return new LoginStatusResponse(false, null);
    }

    public static LoginStatusResponse login(User user){
        return new LoginStatusResponse(true, user);
    }

}
