package org.zhq.security;

import com.netflix.ribbon.proxy.annotation.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@SpringBootApplication
@EnableZuulProxy
@RestController
@Slf4j
public class IsAdminApiApplication {

    private RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(IsAdminApiApplication.class, args);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/me")
    public TokenInfo me(HttpServletRequest request){
        TokenInfo tokenInfo = (TokenInfo) request.getSession().getAttribute("token");
        return tokenInfo;
    }

    @GetMapping("/oauth/callback")
    public void callback(@RequestParam String code, String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("state is "+state);
        //认证服务器验证token url请求
        String oauthServiceUrl = "http://gateway.zhq.com:9070/token/oauth/token";
        //装载请求头信息[contentType,basicAuth] 请求体[token]
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBasicAuth("admin","123456");
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("code",code);
        params.add("grant_type","authorization_code");
        params.add("redirect_url","http://admin.zhq.com:8080/oauth/callback");
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,httpHeaders);
        //restTemplate发送请求获取tokenInfo对象
        ResponseEntity<TokenInfo> responseEntity = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST,entity,TokenInfo.class);
//        request.getSession().setAttribute("token",responseEntity.getBody().init());
        Cookie accessTokenCookie = new Cookie("zhq_access_token",responseEntity.getBody().getAccess_token());
        accessTokenCookie.setMaxAge(responseEntity.getBody().getExpires_in().intValue());
        accessTokenCookie.setDomain("zhq.com");
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("zhq_refresh_token",responseEntity.getBody().getRefresh_token());
        refreshTokenCookie.setMaxAge(2592000);
        refreshTokenCookie.setDomain("zhq.com");
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        response.sendRedirect("/");
    }

}
