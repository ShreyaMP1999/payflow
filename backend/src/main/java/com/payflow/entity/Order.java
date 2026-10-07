package com.payflow.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
  public enum Status { PENDING, PAID, FAILED }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id", nullable=false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private Status status;

  @Column(name="total_cents", nullable=false)
  private Integer totalCents;

  @Column(nullable=false)
  private String currency;

  @Column(name="stripe_session_id")
  private String stripeSessionId;

  @Column(name="created_at", nullable=false)
  private Instant createdAt = Instant.now();

  @OneToMany(mappedBy="order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  public Long getId() { return id; }
  public Long getUserId() { return userId; }
  public Status getStatus() { return status; }
  public Integer getTotalCents() { return totalCents; }
  public String getCurrency() { return currency; }
  public String getStripeSessionId() { return stripeSessionId; }
  public List<OrderItem> getItems() { return items; }

  public void setUserId(Long userId) { this.userId = userId; }
  public void setStatus(Status status) { this.status = status; }
  public void setTotalCents(Integer totalCents) { this.totalCents = totalCents; }
  public void setCurrency(String currency) { this.currency = currency; }
  public void setStripeSessionId(String stripeSessionId) { this.stripeSessionId = stripeSessionId; }
}
