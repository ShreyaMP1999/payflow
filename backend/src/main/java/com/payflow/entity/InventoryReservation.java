package com.payflow.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_reservations")
public class InventoryReservation {
  public enum Status { ACTIVE, RELEASED, CONSUMED }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id", nullable=false)
  private Long userId;

  @Column(name="product_id", nullable=false)
  private Long productId;

  @Column(nullable=false)
  private Integer quantity;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private Status status;

  @Column(name="expires_at", nullable=false)
  private Instant expiresAt;

  public Long getId() { return id; }
  public Long getUserId() { return userId; }
  public Long getProductId() { return productId; }
  public Integer getQuantity() { return quantity; }
  public Status getStatus() { return status; }
  public Instant getExpiresAt() { return expiresAt; }

  public void setUserId(Long userId) { this.userId = userId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public void setStatus(Status status) { this.status = status; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
