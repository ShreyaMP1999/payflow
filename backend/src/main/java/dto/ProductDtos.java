package com.payflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductDtos {

  public record CreateProductRequest(
      @NotBlank String name,
      String description,
      @NotNull @Min(1) Integer priceCents,
      @NotNull @Min(0) Integer stock
  ) {}

  public record ProductResponse(
      Long id,
      String name,
      String description,
      Integer priceCents,
      Integer stock
  ) {}
}
