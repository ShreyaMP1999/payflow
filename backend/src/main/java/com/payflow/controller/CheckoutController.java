package com.payflow.controller;

import com.payflow.dto.CheckoutDtos;
import com.payflow.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
  private final CheckoutService checkoutService;

  public CheckoutController(CheckoutService checkoutService) {
    this.checkoutService = checkoutService;
  }

  @PostMapping("/session")
  public CheckoutDtos.CheckoutResponse createSession(Authentication auth, @Valid @RequestBody CheckoutDtos.CheckoutRequest req) throws Exception {
    String email = auth.getName();
    return checkoutService.createCheckout(email, req);
  }
}
