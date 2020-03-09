package org.zhq.security;

import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static void clearCookie(HttpServletRequest request, HttpServletResponse response,String ...names){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if(StringUtils.isNotBlank(cookieName)){
                    for (String name : names) {
                        if(cookieName.equals(name)){
                            Cookie c = new Cookie(cookieName, "");
                            c.setMaxAge(0);
                            c.setPath(request.getContextPath());
                            c.setDomain(request.getServerName());
                            response.addCookie(c);
                        }
                    }
                }
            }
        }
    }

    public static String getCookie(String param) {
        String result = null;
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(StringUtils.equals(param,cookie.getName())){
                result = cookie.getValue();
                break;
            }
        }
        return result;
    }
}
