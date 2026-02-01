package com.payflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CheckoutDtos {
  public record CartItem(
      @NotNull Long productId,
      @NotNull @Min(1) Integer quantity
  ) {}

  public record CheckoutRequest(
      @NotEmpty List<CartItem> items
  ) {}

  public record CheckoutResponse(
      String checkoutUrl
  ) {}
}
