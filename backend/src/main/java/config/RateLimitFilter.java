package com.payflow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
  private static class Bucket { int count; long windowStartEpochSec; }

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
  private final int limitPerMinute = 120;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String key = req.getRemoteAddr() + ":" + (req.getUserPrincipal() == null ? "anon" : req.getUserPrincipal().getName());
    long now = Instant.now().getEpochSecond();
    Bucket b = buckets.computeIfAbsent(key, k -> { Bucket nb = new Bucket(); nb.windowStartEpochSec = now; return nb; });

    synchronized (b) {
      if (now - b.windowStartEpochSec >= 60) {
        b.windowStartEpochSec = now;
        b.count = 0;
      }
      b.count++;
      if (b.count > limitPerMinute) {
        res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        res.getWriter().write("Too many requests");
        return;
      }
    }

    chain.doFilter(req, res);
  }
}
