package org.zhq.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@SpringBootApplication
@RestController
public class IsAdminApiApplication {

    private RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(IsAdminApiApplication.class, args);
    }

    @PostMapping("/login")
    public void login(@RequestBody Credentials credentials, HttpSession session){
        //认证服务器验证token url请求
        String authServiceUrl = "http://localhost:9070/token/oauth/token";
        //装载请求头信息[contentType,basicAuth] 请求体[token]
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBasicAuth("admin","123456");
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("username",credentials.getUsername());
        params.add("password",credentials.getPassword());
        params.add("grant_type","password");
        params.add("scope","read write");
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,httpHeaders);
        //restTemplate发送请求获取tokenInfo对象
        ResponseEntity<TokenInfo> response = restTemplate.exchange(authServiceUrl, HttpMethod.POST,entity,TokenInfo.class);
        session.setAttribute("token",response.getBody());
    }

}
