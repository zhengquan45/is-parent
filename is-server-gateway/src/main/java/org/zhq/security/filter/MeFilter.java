package org.zhq.security.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
public class MeFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 4;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        return StringUtils.equals(request.getRequestURI(),"/user/me");
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String username = currentContext.getZuulRequestHeaders().get("username");
        if(StringUtils.isNotBlank(username)){
            currentContext.setResponseBody("{\"username\":"+username+"}");
        }
        currentContext.setSendZuulResponse(false);
        currentContext.setResponseStatusCode(200);
        currentContext.getResponse().setContentType("application/json");
        return null;
    }
}
