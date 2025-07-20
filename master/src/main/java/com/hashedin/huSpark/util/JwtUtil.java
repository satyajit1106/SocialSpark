package com.hashedin.huSpark.util;

import com.hashedin.huSpark.exception.JwtExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtUtil {
    private final String JWT_SECRET = "hIMT00-QPNb_5IgxwEmmNN8cJQjHgzq_IaDkUJ3h7rE";
    private final Integer JWT_EXPIRY = 36_00_000;

    public SecretKey generateKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        return Jwts
                .builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRY))
                .signWith(generateKey())
                .compact();
    }

    public String decodeToken(String token)  {

        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(token);

            Date expiration = claims.getPayload().getExpiration();

            if(expiration.before(new Date())) {
                throw new JwtExpiredException("JWT Token expired!");
            }

            return claims.getPayload().getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token has expired!");
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }
}
