package com.lgh.pull.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lgh on 2016/1/12.
 */
public class WebHandlerExceptionResolver implements HandlerExceptionResolver {
    private static Log log = LogFactory.getLog(WebHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestURI = request.getRequestURI().substring(request.getContextPath().length());

        if (requestURI.startsWith("/man")) {
            try {
                throw ex;
            } catch (Exception e) {
                log.error("admin error", e);
            }
            return new ModelAndView("redirect:/404error");
        } else {
            try {
                throw ex;
            } catch (Exception e) {
                log.error("web error", e);
            }
            return new ModelAndView("redirect:/404error");
        }
    }
}
