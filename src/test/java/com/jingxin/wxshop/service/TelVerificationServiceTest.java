package com.jingxin.wxshop.service;

import com.jingxin.wxshop.AuthController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelVerificationServiceTest {
  public static final AuthController.TelAndCode VALID_PARAMETER =
      new AuthController.TelAndCode("12345678901", null);
  public static final AuthController.TelAndCode INVALID_PARAMETER =
          new AuthController.TelAndCode("12312", null);
  public static final AuthController.TelAndCode EMPTY_TEL_PARAMETER =
          new AuthController.TelAndCode(null, null);
  @Test
  public void returnTrueWhenTelIsValid() {
    TelVerificationService telVerificationService = new TelVerificationService();
    assertTrue(telVerificationService.verifyTelParameter(VALID_PARAMETER));
  }

  @Test
  public void returnFalseWhenTelIsInvalid() {
    TelVerificationService telVerificationService = new TelVerificationService();
    assertFalse(telVerificationService.verifyTelParameter(INVALID_PARAMETER));
    assertFalse(telVerificationService.verifyTelParameter(EMPTY_TEL_PARAMETER));
    assertFalse(telVerificationService.verifyTelParameter(null));
  }
}
