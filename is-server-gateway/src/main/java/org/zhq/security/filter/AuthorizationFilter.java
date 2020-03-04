package org.zhq.security.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Random;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
@Slf4j
public class AuthorizationFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        log.info("authorization start");
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //判断当前请求是否需要身份认证
        if (isNeedAuth(request)) {
            TokenInfo tokenInfo = (TokenInfo) request.getAttribute("tokenInfo");
            //判断当前token是否为空、有效
            if(tokenInfo!=null && tokenInfo.isActive()){
                //判断请求是否有权限
                if(!hasPermission(tokenInfo,request)){
                    log.info("audit log update fail 403");
                    handleError(403,requestContext);
                }
                //往RequestHeader里传username以便后续使用
                requestContext.addZuulRequestHeader("username",tokenInfo.getUser_name());
            }else{
                if(!StringUtils.startsWith(request.getRequestURI(),"/token")){
                    log.info("audit log update fail 401");
                    handleError(401,requestContext);
                }
            }
        }
        return null;
    }

    private boolean hasPermission(TokenInfo tokenInfo, HttpServletRequest request) {
        return true;
    }

    private void handleError(int responseCode, RequestContext requestContext) {
        requestContext.getResponse().setContentType("application/json");
        requestContext.setResponseStatusCode(responseCode);
        requestContext.setResponseBody("{\"message\":\"auth fail\"}");
        //请求会到此为止，不会调用业务服务
        requestContext.setSendZuulResponse(false);
    }

    private boolean isNeedAuth(HttpServletRequest request) {
        return true;
    }
}
