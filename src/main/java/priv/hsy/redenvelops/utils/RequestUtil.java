package priv.hsy.redenvelops.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
public class RequestUtil {

    public static String getPathUrl() {
        return getRequest().getServletPath();
    }

    public static String getPathMethod() {
        return getRequest().getMethod().toLowerCase();
    }

    public static String getRequestParam(String key) {
        return getRequest().getParameter(key);
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static void setResponseStatus(Integer httpStatus) {
        getResponse().setStatus(httpStatus);
    }

}
