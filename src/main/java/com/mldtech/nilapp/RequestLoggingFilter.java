package com.mldtech.nilapp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {

            log.warn("UNKNOWN REQUEST DETECTED");
            log.warn("Method: {}", request.getMethod());
            log.warn("URI: {}", request.getRequestURI());
            log.warn("Remote Address: {}", request.getRemoteAddr());
            log.warn("User Agent: {}", request.getHeader("User-Agent"));
            log.warn("Referer: {}", request.getHeader("Referer"));
            log.warn("Origin: {}", request.getHeader("Origin"));
            log.warn("Forwarded For: {}", request.getHeader("X-Forwarded-For"));
            log.warn("All Headers: {}", Collections.list(
                    request.getHeaderNames()
            ).stream().collect(Collectors.toMap(
                    Function.identity(),
                    request::getHeader
            )));
        }
    }
}
