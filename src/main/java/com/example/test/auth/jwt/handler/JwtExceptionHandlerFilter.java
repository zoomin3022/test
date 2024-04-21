package com.example.test.auth.jwt.handler;

import com.example.test.auth.jwt.exception.TokenException;
import com.google.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 스프링에서 제공하는 ExceptionHandler는 ControllerAdvice
 * 즉 컨트롤러 단계에서 적용되므로 필터단계에서는 적용되지 않음
 * 따라서 직접 ExceptionHandler를 구현해야돼서 만든 핸들러필터
 */
@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            log.error("--------------------------------");
            log.error("URL = {}",request.getRequestURL());
            log.error("--------------------------------");
            log.error("StackTrace = {} ", (Object) e.getStackTrace());
            log.error("HttpStatus = {} ", e.getCustomExceptionType().getHttpStatus());
            log.error("Code = {} ", e.getCustomExceptionType().getCode());
            log.error("Message = {} ", e.getCustomExceptionType().getMessage());
            log.error("--------------------------------");

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(e.getCustomExceptionType().getHttpStatus().value());
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("code", e.getCustomExceptionType().getCode());
            responseJson.addProperty("message", e.getCustomExceptionType().getMessage());
            response.getWriter().print(responseJson);
        }
    }
}
