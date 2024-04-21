package com.example.test.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    private static final String BEARER = "bearer";
    private static final String JWT = "JWT";

    @Bean
    public OpenAPI openAPI() {

        Server localServer = new Server().description("로컬 서버").url("http://localhost:8080");
        Server testServer = new Server().description("배포된 테스트 서버").url("https://joo-api.store");

        Info info = new Info()
                .title("Oauth2 테스트용")
                .version("1.0.0")
                .description("Oauth2 테스트용")
                .contact(new Contact() // 연락처
                        .name("zoomin3022")
                        .email("jjwm0128@naver.com")
                        .url("https://github.com/zoomin3022"));

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT)
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        // Security 요청 설정
        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList(JWT);

        return new OpenAPI()
                // Security 인증 컴포넌트 설정
                .components(new Components().addSecuritySchemes(JWT, bearerAuth))
                .addServersItem(testServer)
                .addServersItem(localServer)
                // API 마다 Security 인증 컴포넌트 설정
                .addSecurityItem(addSecurityItem)
                .info(info);
    }
}