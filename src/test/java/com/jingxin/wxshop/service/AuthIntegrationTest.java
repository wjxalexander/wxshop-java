package com.jingxin.wxshop.service;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.jingxin.wxshop.WxshopApplication;
import com.jingxin.wxshop.entity.LoginStatusResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class AuthIntegrationTest {
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

  private static class HttpResponse {
    int code;
    String body;
    Map<String, List<String>> headers;

    HttpResponse(int code, String body, Map<String, List<String>> headers) {
      this.code = code;
      this.body = body;
      this.headers = headers;
    }
  }

  private HttpResponse doHttpRequest(
      String apiName, String method, Object httpRequestBody, String cookie)
      throws JsonProcessingException {
    HttpRequest requestBuilder =
        method == "get" ? HttpRequest.get(getUrl(apiName)) : HttpRequest.post(getUrl(apiName));
    requestBuilder
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE);
    if (cookie != null) {
      requestBuilder.header("Cookie", cookie);
    }
    if (httpRequestBody != null) {
      requestBuilder.send(objectMapper.writeValueAsString(httpRequestBody));
    }
    return new HttpResponse(requestBuilder.code(), requestBuilder.body(), requestBuilder.headers());
  }

  private void assertNotLoginAtBeginning() throws JsonProcessingException {
    String statusResponse = doHttpRequest("/api/status", "get", null, null).body;
    LoginStatusResponse response =
            objectMapper.readValue(statusResponse, LoginStatusResponse.class);
    Assertions.assertFalse(response.isLogin());
  }
  @Test
  public void loginLogoutTest() throws JsonProcessingException {
    assertNotLoginAtBeginning();
    int responseCode =
        doHttpRequest("/api/code", "post", TelVerificationServiceTest.VALID_PARAMETER, null).code;

    Assertions.assertEquals(responseCode, HTTP_OK);
    Map<String, List<String>> responseHeaders =
        doHttpRequest("/api/login", "post", TelVerificationServiceTest.VALID_PARAMETER_CODE, null)
            .headers;
    List<String> setCookie = responseHeaders.get("Set-Cookie");
    // sid=73908958-7e19-4997-a97a-16f7961afb6e; Path=/; Max-Age=2592000; Expires=Sun, 14-Jan-2024
    // 03:40:59 GMT; HttpOnly; SameSite=lax
    Optional<String> sessionCookie = getCookieByName(setCookie, "JSESSIONID");
    String sessionId = sessionCookie.get().substring(0, sessionCookie.get().indexOf(";"));
    String cookieResponse = doHttpRequest("/api/status", "get", null, sessionId).body;
    LoginStatusResponse responseWithCookie =
        objectMapper.readValue(cookieResponse, LoginStatusResponse.class);
    Assertions.assertTrue(responseWithCookie.isLogin());
    Assertions.assertEquals(
        responseWithCookie.getUser().getTel(),
        TelVerificationServiceTest.VALID_PARAMETER_CODE.getTel());

    doHttpRequest("/api/logout", "post", null, sessionId);
    String logoutResponse = doHttpRequest("/api/status", "get", null, sessionId).body;

    LoginStatusResponse logoutStatus =
        objectMapper.readValue(logoutResponse, LoginStatusResponse.class);
    Assertions.assertFalse(logoutStatus.isLogin());
    Assertions.assertNull(logoutStatus.getUser());
  }

  private Optional<String> getCookieByName(List<String> cookies, String name) {
    return cookies.stream().filter(cookie -> cookie.contains(name)).findFirst();
  }

  private String getUrl(String apiName) {
    return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
  }
}
