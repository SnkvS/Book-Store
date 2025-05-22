package com.senkiv.bookstore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtUtil {
    public static final String JWT_IS_INVALID_OR_EXPIRED = "JWT is invalid or expired.";
    @Value("${token.expiration}")
    private Long expiration;
    @Value("${token.secret}")
    private String secret;
    private SecretKey secretKey;

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String getUsername(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
        Jws<Claims> claims = parser.parseSignedClaims(token);
        return claims.getPayload().getSubject();
    }

    public boolean isValid(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(secretKey)
                    .build();
            Jws<Claims> claims = parser.parseSignedClaims(token);

            return !claims.getPayload().getExpiration()
                    .before(new Date(System.currentTimeMillis()));
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(JWT_IS_INVALID_OR_EXPIRED, e);
        }
    }

    @PostConstruct
    private void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
