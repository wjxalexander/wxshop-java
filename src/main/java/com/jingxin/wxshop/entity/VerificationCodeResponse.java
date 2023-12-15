package com.jingxin.wxshop.entity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VerificationCodeResponse {
    @JsonProperty("code")
    private String code;

    @JsonProperty("code")
    public String getCode() {
        return code;
    }
    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    public VerificationCodeResponse(String code) {
        this.code = code;
    }
}
