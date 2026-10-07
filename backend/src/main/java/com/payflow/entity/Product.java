package com.payflow.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Column(name = "price_cents", nullable = false)
  private Integer priceCents;

  @Column(nullable = false)
  private String currency = "usd";

  @Column(nullable = false)
  private Integer stock;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public Integer getPriceCents() { return priceCents; }
  public String getCurrency() { return currency; }
  public Integer getStock() { return stock; }

  public void setName(String name) { this.name = name; }
  public void setDescription(String description) { this.description = description; }
  public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
  public void setCurrency(String currency) { this.currency = currency; }
  public void setStock(Integer stock) { this.stock = stock; }
}
