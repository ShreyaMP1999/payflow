package com.payflow.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTests {
  @Test
  void returnsRetryGuidanceAndDoesNotBlockAnotherClient() throws Exception {
    RateLimitFilter filter = new RateLimitFilter();
    AtomicInteger accepted = new AtomicInteger();
    MockHttpServletResponse response = null;
    for (int i = 0; i < 121; i++) {
      var request = new MockHttpServletRequest("GET", "/api/products");
      request.setRemoteAddr("192.0.2.1");
      response = new MockHttpServletResponse();
      filter.doFilter(request, response, (req, res) -> accepted.incrementAndGet());
    }
    assertThat(accepted.get()).isEqualTo(120);
    assertThat(response.getStatus()).isEqualTo(429);
    assertThat(Long.parseLong(response.getHeader("Retry-After"))).isBetween(1L, 60L);
    assertThat(response.getContentType()).isEqualTo("application/json");
    assertThat(response.getContentAsString()).contains("\"code\":\"rate_limited\"");

    var other = new MockHttpServletRequest("GET", "/api/products");
    other.setRemoteAddr("192.0.2.2");
    filter.doFilter(other, new MockHttpServletResponse(), (req, res) -> accepted.incrementAndGet());
    assertThat(accepted.get()).isEqualTo(121);
  }
}
