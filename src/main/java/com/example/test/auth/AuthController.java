package com.example.test.auth;

import com.example.test.auth.jwt.exception.TokenExceptionType;
import com.example.test.global.ErrorResponse;
import com.example.test.auth.jwt.JwtTokenUtil;
import com.example.test.auth.jwt.dto.TokenDto;
import com.example.test.auth.jwt.exception.TokenException;
import com.example.test.member.dto.request.MemberRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가입 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "가입 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Operation(summary = "회원 가입", description = "회원 가입 시도")
    //@Parameter dto만을 파라미터로 받으면 해당 dto @Schema를 작성하면 되므로 생략
    @PostMapping("/members")
    public ResponseEntity<String> signUp(@RequestBody @Valid MemberRequestDto.SignUp signUp) {
        authService.signUp(signUp);

        return ResponseEntity.ok(null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "로그인 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Operation(summary = "로그인", description = "로그인 시도")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberRequestDto.Login login, HttpServletResponse response) {

        TokenDto tokenDto = authService.login(login);

        final String refreshToken = tokenDto.getRefreshToken();
        final Long expiration = jwtTokenUtil.getExpiration(refreshToken);
        final Long expirationSecond = expiration / 1000;

        /*
        프론트와 도메인이 달라 이방법을 쓸 수 없으므로 폐기

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(expirationSecond.intValue());
        refreshTokenCookie.setDomain("localhost");
        refreshTokenCookie.setSecure(true); // HTTPS에서만 전송
        refreshTokenCookie.setPath("/"); // 경로 설정
        response.addCookie(refreshTokenCookie);
        */


        return ResponseEntity.ok(tokenDto);
    }

//    @PostMapping("/reissue")
//    public ResponseEntity<String> reissueByCookie(
//            @CookieValue(value = "refreshToken", required = false) Cookie refreshCookie) {
//        if (refreshCookie == null) {
//            throw new TokenException(TokenExceptionType.REFRESH_TOKEN_NOT_EXIST);
//        }
//        String refreshToken = refreshCookie.getValue();
//        String reissuedAccessToken = authService.reissue(refreshToken);
//        return ResponseEntity.ok(reissuedAccessToken);
//    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "재발급 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Operation(summary = "토큰 재발급", description = "토큰 재발급 시도")
    @Parameters({
            @Parameter(name = "refreshToken", description = "리프레쉬 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"),
    })
    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(@RequestParam String refreshToken) {
        String reissuedAccessToken = authService.reissue(refreshToken);
        return ResponseEntity.ok(reissuedAccessToken);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "로그아웃 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Operation(summary = "로그아웃", description = "로그아웃 시도")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(value = "refreshToken", required = false) Cookie refreshCookie,
            HttpServletResponse response) {
        if (refreshCookie == null) {
            throw new TokenException(TokenExceptionType.REFRESH_TOKEN_NOT_EXIST);
        }
        String refreshToken = refreshCookie.getValue();

        authService.logout(refreshToken);

        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(null);
    }
}
