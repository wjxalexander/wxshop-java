package com.jingxin.wxshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.jingxin.wxshop.WxshopApplication;
import com.jingxin.wxshop.entity.LoginStatusResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = WxshopApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
public class AuthIntegrationTest {
    @Autowired
    Environment environment;
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

    @Test
    public void loginLogoutTest() throws JsonProcessingException {
        String statusResponse =
                HttpRequest.get(getUrl("/api/status"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .body();
        LoginStatusResponse response = objectMapper.readValue(statusResponse, LoginStatusResponse.class);
        Assertions.assertFalse(response.isLogin());
        HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(TelVerificationServiceTest.VALID_PARAMETER))
                .code();
        Map<String, List<String>> responseHeaders = HttpRequest.post(getUrl("/api/login"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(TelVerificationServiceTest.VALID_PARAMETER_CODE))
                .headers();

        List<String> setCookie = responseHeaders.get("Set-Cookie");
        //sid=73908958-7e19-4997-a97a-16f7961afb6e; Path=/; Max-Age=2592000; Expires=Sun, 14-Jan-2024 03:40:59 GMT; HttpOnly; SameSite=lax
        Optional<String> sessionCookie = getCookieByName(setCookie, "sid");
        String cookieResponse =
                HttpRequest.get(getUrl("/api/status"))
                        .header("Cookie",sessionCookie.get() )
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .body();
        LoginStatusResponse responseWithCookie = objectMapper.readValue(cookieResponse, LoginStatusResponse.class);

        Assertions.assertTrue(responseWithCookie.isLogin());
        Assertions.assertEquals(responseWithCookie.getUser().getTel(), TelVerificationServiceTest.VALID_PARAMETER_CODE.getTel());
    }
    private Optional<String> getCookieByName(List<String> cookies, String name){
        return cookies.stream().filter(cookie-> cookie.contains("sid")).findFirst();
    }



    private String getUrl(String apiName) {
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }
}
