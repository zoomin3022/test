package com.example.test.global.config;

import com.example.test.auth.jwt.JwtFilter;
import com.example.test.auth.jwt.JwtTokenUtil;
import com.example.test.auth.jwt.handler.CustomAccessDeniedHandler;
import com.example.test.auth.jwt.handler.CustomAuthenticationEntryPoint;
import com.example.test.auth.jwt.handler.JwtExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    public static final String[] AUTH_WHITELIST = {
            "/login", "/reissue", "/authority/**",
            "/", "/error",

            "/favicon.ico",

            /* swagger v3 */
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api-docs",
            "/admins/voting/ballots"
    };

    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setMaxAge(3600L); //1시간
                    return config;
                }))
                .csrf(CsrfConfigurer::disable)
                //csrf는 stateless에서는 불필요 (쿠키사용하지 않으므로 X)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .logout(LogoutConfigurer::disable)
                //기본 인증 로그인과 기본 로그인페이지 사용 안하므로
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //jwt기반 stateless 인증을 하므로
                .authorizeRequests(requests ->
                        requests.requestMatchers(AUTH_WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.POST, "/members").permitAll()
                                .requestMatchers("/admins/**").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/voting").hasAnyRole("USER")
                                .anyRequest().authenticated())
                .exceptionHandling(authenticationManager -> authenticationManager
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterBefore(new JwtFilter(antPathMatcher(), jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(), JwtFilter.class)
                .build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers(AUTH_WHITELIST);
//    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}
