package org.zhq.security.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
@Slf4j
public class OAuthFilter extends ZuulFilter {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public String filterType() {
        //pre post error route
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        //true 意味着run方法会被调用
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        log.info("oauth start");
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if(StringUtils.startsWith(request.getRequestURI(),"/token")){
            return null;
        }
        String authHeader = request.getHeader("Authorization");
        if(StringUtils.isBlank(authHeader)){
            return null;
        }
        if (!StringUtils.startsWithIgnoreCase(authHeader, "bearer ")) {
            return null;
        }
        try {
            TokenInfo info = getTokenInfo(authHeader);
            request.setAttribute("tokenInfo",info);
        } catch (Exception ex){
            log.error("get token info error");
        }
        return null;
    }

    private TokenInfo getTokenInfo(String authHeader){
        //获取token
        String token = StringUtils.substringAfter(authHeader,"bearer ");
        //认证服务器验证token url请求
        String authServiceUrl = "http://localhost:9090/oauth/check_token";
        //装载请求头信息[contentType,basicAuth] 请求体[token]
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBasicAuth("gateway","123456");
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",token);
        HttpEntity<MultiValueMap<String,String>>entity = new HttpEntity<>(params,httpHeaders);
        //restTemplate发送请求获取tokenInfo对象
        ResponseEntity<TokenInfo> response = restTemplate.exchange(authServiceUrl, HttpMethod.POST,entity,TokenInfo.class);
        log.info("token info is "+response.toString());
        return response.getBody();
    }
}
