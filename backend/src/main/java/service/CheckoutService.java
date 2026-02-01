package com.payflow.service;

import com.payflow.dto.CheckoutDtos;
import com.payflow.entity.InventoryReservation;
import com.payflow.entity.Order;
import com.payflow.entity.OrderItem;
import com.payflow.entity.Product;
import com.payflow.entity.User;
import com.payflow.repository.InventoryReservationRepository;
import com.payflow.repository.OrderRepository;
import com.payflow.repository.ProductRepository;
import com.payflow.repository.UserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final InventoryReservationRepository reservationRepository;
  private final OrderRepository orderRepository;
  private final StripeService stripeService;

  public CheckoutService(
      UserRepository userRepository,
      ProductRepository productRepository,
      InventoryReservationRepository reservationRepository,
      OrderRepository orderRepository,
      StripeService stripeService
  ) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
    this.reservationRepository = reservationRepository;
    this.orderRepository = orderRepository;
    this.stripeService = stripeService;
  }

  @Transactional
  public CheckoutDtos.CheckoutResponse createCheckout(String email, CheckoutDtos.CheckoutRequest req) throws Exception {
    User user = userRepository.findByEmail(email).orElseThrow();

    // cleanup expired reservations (simple approach for MVP)
    releaseExpiredReservations();

    List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
    int total = 0;

    // Reserve inventory with row-locking on product
    List<InventoryReservation> reservations = new ArrayList<>();
    for (CheckoutDtos.CartItem item : req.items()) {
      Product p = productRepository.findByIdForUpdate(item.productId()).orElseThrow();

      if (p.getStock() < item.quantity()) {
        throw new IllegalArgumentException("Insufficient stock for productId=" + p.getId());
      }

      // reserve: decrement stock inside the same tx
      p.setStock(p.getStock() - item.quantity());

      InventoryReservation r = new InventoryReservation();
      r.setUserId(user.getId());
      r.setProductId(p.getId());
      r.setQuantity(item.quantity());
      r.setStatus(InventoryReservation.Status.ACTIVE);
      r.setExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES));
      reservations.add(reservationRepository.save(r));

      total += p.getPriceCents() * item.quantity();

      lineItems.add(
          SessionCreateParams.LineItem.builder()
              .setQuantity((long) item.quantity())
              .setPriceData(
                  SessionCreateParams.LineItem.PriceData.builder()
                      .setCurrency(p.getCurrency())
                      .setUnitAmount((long) p.getPriceCents())
                      .setProductData(
                          SessionCreateParams.LineItem.PriceData.ProductData.builder()
                              .setName(p.getName())
                              .build()
                      )
                      .build()
              )
              .build()
      );
    }

    // Create an order in PENDING state before sending to Stripe
    Order order = new Order();
    order.setUserId(user.getId());
    order.setStatus(Order.Status.PENDING);
    order.setTotalCents(total);
    order.setCurrency("usd");
    order = orderRepository.save(order);

    // attach order items
    for (CheckoutDtos.CartItem item : req.items()) {
      Product p = productRepository.findById(item.productId()).orElseThrow();
      OrderItem oi = new OrderItem();
      oi.setOrder(order);
      oi.setProductId(p.getId());
      oi.setQuantity(item.quantity());
      oi.setPriceCents(p.getPriceCents());
      order.getItems().add(oi);
    }

    Session session = stripeService.createCheckoutSession(order.getId().toString(), lineItems);
    order.setStripeSessionId(session.getId());

    // idempotency/resiliency note: for a portfolio MVP, Stripe checkout session id acts as reference.
    return new CheckoutDtos.CheckoutResponse(session.getUrl());
  }

  private void releaseExpiredReservations() {
    var expired = reservationRepository.findByStatusAndExpiresAtBefore(
        InventoryReservation.Status.ACTIVE, Instant.now()
    );
    // For MVP: we release by adding stock back.
    for (var r : expired) {
      productRepository.findByIdForUpdate(r.getProductId()).ifPresent(p -> {
        p.setStock(p.getStock() + r.getQuantity());
      });
      r.setStatus(InventoryReservation.Status.RELEASED);
    }
  }
}
