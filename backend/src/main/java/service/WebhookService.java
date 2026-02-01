package com.payflow.service;

import com.payflow.entity.InventoryReservation;
import com.payflow.entity.Order;
import com.payflow.repository.InventoryReservationRepository;
import com.payflow.repository.OrderRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class WebhookService {
  private final String webhookSecret;
  private final OrderRepository orderRepository;
  private final InventoryReservationRepository reservationRepository;

  public WebhookService(
      @Value("${stripe.webhookSecret}") String webhookSecret,
      OrderRepository orderRepository,
      InventoryReservationRepository reservationRepository
  ) {
    this.webhookSecret = webhookSecret;
    this.orderRepository = orderRepository;
    this.reservationRepository = reservationRepository;
  }

  @Transactional
  public void handleStripeEvent(String payload, String sigHeader) {
    Event event;
    try {
      event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
    } catch (Exception e) {
      throw new RuntimeException("Invalid webhook signature", e);
    }

    if ("checkout.session.completed".equals(event.getType())) {
      Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
      if (session == null) return;

      String orderId = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;
      if (orderId == null) return;

      Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
      if (order == null) return;

      order.setStatus(Order.Status.PAID);
      orderRepository.save(order);

      // For MVP: consume any active reservations for this user (a tighter design would link reservations -> order)
      var expiredNow = Instant.now(); // placeholder; keep simple
      var actives = reservationRepository.findByStatusAndExpiresAtBefore(InventoryReservation.Status.ACTIVE, expiredNow.plusSeconds(60*60*24*365));
      for (var r : actives) {
        if (r.getUserId().equals(order.getUserId())) {
          r.setStatus(InventoryReservation.Status.CONSUMED);
        }
      }
    }
  }
}
