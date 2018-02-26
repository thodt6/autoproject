/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author thond
 */
public class SessionTimeoutFilter implements Filter {

    private final Logger logger = Logger.getLogger(SessionTimeoutFilter.class);

    private String timeoutPage = "index.jsp";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {

        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            // is session expire control required for this request?
            if (isSessionControlRequiredForThisResource(httpServletRequest)) {

                // is session invalid?
                if (isSessionInvalid(httpServletRequest)) {
                    String timeoutUrl = httpServletRequest.getContextPath() + "/" + getTimeoutPage();
                    logger.info("session is invalid! redirecting to timeoutpage : " + timeoutUrl);

                    httpServletResponse.sendRedirect(timeoutUrl);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     *
     * session shouldn't be checked for some pages. For example: for timeout
     * page.. Since we're redirecting to timeout page from this filter, if we
     * don't disable session control for it, filter will again redirect to it
     * and this will be result with an infinite loop...
     */
    private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
        String requestPath = httpServletRequest.getRequestURI();

        boolean controlRequired = !StringUtils.contains(requestPath, getTimeoutPage());
        return controlRequired;
    }

    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();
        return sessionInValid;
    }

    public void destroy() {
    }

    public String getTimeoutPage() {
        return timeoutPage;
    }

    public void setTimeoutPage(String timeoutPage) {
        this.timeoutPage = timeoutPage;
    }

}
