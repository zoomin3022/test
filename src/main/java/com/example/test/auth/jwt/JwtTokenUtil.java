package com.example.test.auth.jwt;

import com.example.test.auth.jwt.dto.TokenDto;
import com.example.test.auth.jwt.exception.TokenException;
import com.example.test.auth.jwt.exception.TokenExceptionType;
import com.example.test.member.entity.Member;
import com.example.test.member.dto.request.MemberRequestDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    private static final String AUTHORITIES_KEY = "authorities";
    private static final String NAME_KEY = "email";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;     // 개발 편의상 프론트 재발급 구현 전에는 길게
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일
    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(Authentication authentication) {

        final String authoritiesString = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Instant now = Instant.now();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .claim(NAME_KEY, authentication.getName())
                .claim(AUTHORITIES_KEY, authoritiesString)
                .setExpiration(Date.from(now.plus(ACCESS_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(REFRESH_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDto generateToken(Member member) {

        final String authoritiesString = member.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Instant now = Instant.now();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .claim(NAME_KEY, member.getMemberEmail())
                .claim(AUTHORITIES_KEY, authoritiesString)
                .setExpiration(Date.from(now.plus(ACCESS_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(REFRESH_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String reissueAccessToken(Member member) {
        final String authoritiesString = member.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Instant now = Instant.now();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .claim(NAME_KEY, member.getMemberEmail())
                .claim(AUTHORITIES_KEY, authoritiesString)
                .setExpiration(Date.from(now.plus(ACCESS_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return accessToken;
    }

    public String getEmail(Claims claims) {
        return claims.get(NAME_KEY).toString();
    }

    public Authentication getAuthentication(Claims claims) {

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new TokenException(TokenExceptionType.AUTHORITY_NOT_EXIST_TOKEN);
        }
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        List<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        MemberRequestDto.Access principal = MemberRequestDto.Access.from(getEmail(claims), authorityStrings);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenExceptionType.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new TokenException(TokenExceptionType.INVALID_TOKEN);
        }
    }

    public Claims parseAccessTokenClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenExceptionType.EXPIRED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new TokenException(TokenExceptionType.INVALID_ACCESS_TOKEN);
        }
    }

    public Claims parseRefreshTokenClaims(String refreshToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenExceptionType.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
            throw new TokenException(TokenExceptionType.INVALID_REFRESH_TOKEN);
        }
    }

    public Long getExpiration(String token) {

        Claims claims = parseClaims(token);

        Date expiration = claims.getExpiration();
        Long now = Date.from(Instant.now()).getTime();
        return (expiration.getTime() - now);
    }

}
