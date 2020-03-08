package org.zhq.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
public class SessionTokenFilter extends ZuulFilter
{
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        TokenInfo token = (TokenInfo) request.getSession().getAttribute("token");
        if(token!=null){
            //refresh token logic
            String tokenValue = token.getAccess_token();
            if(token.isExpired()){
                //认证服务器验证token url请求
                String oauthServiceUrl = "http://gateway.zhq.com:9070/token/oauth/token";
                //装载请求头信息[contentType,basicAuth] 请求体[token]
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.setBasicAuth("admin","123456");

                MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
                params.add("grant_type","refresh_token");
                params.add("refresh_token",token.getRefresh_token());
                HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,httpHeaders);

                //restTemplate发送请求获取tokenInfo对象
                ResponseEntity<TokenInfo> responseEntity = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST,entity,TokenInfo.class);
                request.getSession().setAttribute("token",responseEntity.getBody().init());
                tokenValue = responseEntity.getBody().getAccess_token();
            }
            requestContext.addZuulRequestHeader("Authorization","bearer " + tokenValue);
        }
        return null;
    }
}
