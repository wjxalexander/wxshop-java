package com.jingxin.wxshop.service;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.jingxin.wxshop.WxshopApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = WxshopApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
public class CodeIntegrationTest {
  @Autowired Environment environment;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void returnHttpOkWhenParameterIsCorrect() throws JsonProcessingException {
    int responseCode =
        HttpRequest.post(getUrl("/api/code"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .send(objectMapper.writeValueAsString(TelVerificationServiceTest.VALID_PARAMETER))
            .code();
    Assertions.assertEquals(HTTP_OK, responseCode);
  }

  @Test
  public void returnHttpBadRequestWhenParameterIsInvalid() throws JsonProcessingException {
    int responseCode =
        HttpRequest.post(getUrl("/api/code"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .send(objectMapper.writeValueAsString(TelVerificationServiceTest.INVALID_PARAMETER))
            .code();
    Assertions.assertEquals(HTTP_BAD_REQUEST, responseCode);
  }

  private String getUrl(String apiName) {
    return "http://localhost:" + environment.getProperty("local.server.port")  + apiName;
  }
}
