package com.payflow.controller;

import com.payflow.entity.Product;
import com.payflow.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductRepository repo;

  public ProductController(ProductRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Product> list() {
    return repo.findAll();
  }

  @PostMapping
  public Product create(@RequestBody Product p) {
    // For MVP: no admin auth; in “real” prod add ROLE_ADMIN.
    return repo.save(p);
  }
}
