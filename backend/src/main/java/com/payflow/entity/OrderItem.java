package com.payflow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name="order_id")
  private Order order;

  @Column(name="product_id", nullable=false)
  private Long productId;

  @Column(nullable=false)
  private Integer quantity;

  @Column(name="price_cents", nullable=false)
  private Integer priceCents;

  public void setOrder(Order order) { this.order = order; }
  public void setProductId(Long productId) { this.productId = productId; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
}
