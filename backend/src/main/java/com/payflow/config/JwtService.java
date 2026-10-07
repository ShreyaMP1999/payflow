package com.payflow.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey key;
  private final long ttlMinutes;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.ttlMinutes}") long ttlMinutes
  ) {
    // For production: use >= 32 bytes secret (HS256).
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.ttlMinutes = ttlMinutes;
  }

  public String issueToken(String subjectEmail) {
    Instant now = Instant.now();
    Instant exp = now.plus(ttlMinutes, ChronoUnit.MINUTES);

    return Jwts.builder()
        .subject(subjectEmail)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public String parseSubject(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }
}
