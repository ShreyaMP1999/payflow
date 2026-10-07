package com.payflow.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {

  private final String appUrl;

  public StripeService(
      @Value("${stripe.secretKey}") String secretKey,
      @Value("${stripe.appUrl}") String appUrl
  ) {
    Stripe.apiKey = secretKey;
    this.appUrl = appUrl;
  }

  public Session createCheckoutSession(String orderId, List<SessionCreateParams.LineItem> lineItems) throws Exception {
    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(appUrl + "/success?orderId=" + orderId)
        .setCancelUrl(appUrl + "/cart")
        .putMetadata("orderId", orderId)
        .addAllLineItem(lineItems)
        .build();

    return Session.create(params);
  }
}
