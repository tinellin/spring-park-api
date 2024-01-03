package com.park.demoparkapi.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtUtils {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization ";
    private static final String SECRET_KEY = "CwFQTbzBFXG82JU4cCYLm5EyfOe7bjBA";
    private static final long EXPIRE_DAYS = 0;
    private static final long EXPIRE_HOURS = 0;
    private static final long EXPIRE_MINUTES = 2;

    private JwtUtils () {}

    /* Gerar chave privada */
    private static SecretKey generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /* Gerar a data de expiração (YYYY-MM-DD) */
    private static Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); /* (YYYY-MM-DD HH::MM:SS) */
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);

        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    /* Criar token JWT */
    public static JwtToken createJwtToken(String username, String role) {
        Date issuedAt = new Date();
        Date limit = toExpireDate(issuedAt);

        String token = Jwts.builder()
                .header().add("typ", "JWT")
                .and()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(limit)
                .signWith(generateKey())
                .claim("role", role)
                .compact();

        return new JwtToken(token);
    }

    /* Método para retornar o payload do token jwt */
    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(refactorToken(token)).getPayload();
        } catch (JwtException ex) {
            log.error(String.format("Invalid token %s", ex.getMessage()));
        }

        return null;
    }

    /* Método para retornar se o token jwt é válido */
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(refactorToken(token));
            return true;
        } catch (JwtException ex) {
            log.error(String.format("Invalid token %s", ex.getMessage()));
        }
        
        return false;
    }

    /* Remover "Bearer" de "Bearer <token>" se haver */
    private static String refactorToken(String token) {
        return token.contains(JWT_BEARER) ? token.substring(JWT_BEARER.length()) : token;
    }

    /* Testar a validade do token */
    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
}