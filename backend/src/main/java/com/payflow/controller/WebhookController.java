package com.payflow.controller;

import com.payflow.service.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
  private final WebhookService webhookService;

  public WebhookController(WebhookService webhookService) {
    this.webhookService = webhookService;
  }

  @PostMapping(value="/stripe", consumes = MediaType.APPLICATION_JSON_VALUE)
  public String stripe(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sig)
      throws SignatureVerificationException {
    webhookService.handleStripeEvent(payload, sig);
    return "ok";
  }
}
