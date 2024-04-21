package com.example.test.auth.jwt;


import com.example.test.auth.jwt.exception.TokenException;
import com.example.test.auth.jwt.exception.TokenExceptionType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Arrays;

import static com.example.test.global.config.WebSecurityConfig.AUTH_WHITELIST;


@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_TYPE_PREFIX = "Bearer ";
    private final AntPathMatcher antPathMatcher;
    private final JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = extractToken(authenticationHeader);
        Claims claims = jwtTokenUtil.parseAccessTokenClaims(accessToken);
        //여기서 유효성도 같이 검사 (기간, 싸인 등등)
        Authentication authentication = jwtTokenUtil.getAuthentication(claims);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return Arrays.stream(AUTH_WHITELIST).anyMatch(pattern -> antPathMatcher.match(pattern, path) ||
                (request.getMethod().equalsIgnoreCase("POST") && path.equals("/members")));
    }

    private String extractToken(String authenticationHeader) {
        if (authenticationHeader == null) {
            throw new TokenException(TokenExceptionType.ACCESS_TOKEN_NOT_EXIST);
        } else if (!authenticationHeader.startsWith(BEARER_TYPE_PREFIX)) {
            throw new TokenException(TokenExceptionType.INVALID_ACCESS_TOKEN);
        }
        return authenticationHeader.substring(BEARER_TYPE_PREFIX.length());
    }
}
