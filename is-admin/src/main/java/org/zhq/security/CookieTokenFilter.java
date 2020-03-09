package org.zhq.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
public class CookieTokenFilter extends ZuulFilter
{
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        return !StringUtils.equals(request.getRequestURI(), "/logout");
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();

        String accessToken = CookieUtils.getCookie("zhq_access_token");
        if(StringUtils.isNotBlank(accessToken)){
            requestContext.addZuulRequestHeader("Authorization","bearer " + accessToken);
        }else{
            String refreshToken = CookieUtils.getCookie("zhq_refresh_token");
            if(StringUtils.isNotBlank(refreshToken)){
                //认证服务器验证token url请求
                String oauthServiceUrl = "http://gateway.zhq.com:9070/token/oauth/token";
                //装载请求头信息[contentType,basicAuth] 请求体[token]
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.setBasicAuth("admin","123456");

                MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
                params.add("grant_type","refresh_token");
                params.add("refresh_token",refreshToken);
                HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,httpHeaders);

                //restTemplate发送请求获取tokenInfo对象
                try {
                    ResponseEntity<TokenInfo> responseEntity = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST,entity,TokenInfo.class);

                    requestContext.addZuulRequestHeader("Authorization","bearer " + responseEntity.getBody().getAccess_token());

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

                } catch (RestClientException e) {
                    requestContext.setSendZuulResponse(false);
                    requestContext.setResponseStatusCode(500);
                    requestContext.setResponseBody("{\"message\":\"refresh fail\"}");
                    requestContext.getResponse().setContentType("application/json");
                }
            }else{
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(500);
                requestContext.setResponseBody("{\"message\":\"refresh fail\"}");
                requestContext.getResponse().setContentType("application/json");
            }
        }
        return null;
    }


}
