package com.example.movie.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // THAY bằng secret trong application.yml nếu muốn inject, ở đây demo cứng:
    private final Key key = Keys.hmacShaKeyFor("CHANGE_ME_SUPER_SECRET_CHANGE_ME_SUPER_SECRET".getBytes());
    private final long expirationMs = 60L * 60L * 1000L; // 60m

    public String generate(String subject) {
        final Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
