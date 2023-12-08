package com.jingxin.wxshop.service;

import com.jingxin.wxshop.AuthController;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class TelVerificationService {
  private static final Pattern TEL_PATTERN = Pattern.compile("1\\d{10}");

  public boolean verifyTelParameter(AuthController.TelAndCode telAndCode) {
    if (telAndCode == null || telAndCode.getTel() == null) {
      return false;
    }
    return TEL_PATTERN.matcher(telAndCode.getTel()).find();
  }
}
