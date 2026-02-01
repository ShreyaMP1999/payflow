package com.payflow.service;

import com.payflow.dto.ProductDtos;
import com.payflow.entity.Product;
import com.payflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

  private final ProductRepository repo;

  public ProductService(ProductRepository repo) {
    this.repo = repo;
  }

  public Product create(ProductDtos.CreateProductRequest req) {
    Product p = new Product();
    p.setName(req.name());
    p.setDescription(req.description());
    p.setPriceCents(req.priceCents());
    p.setStock(req.stock());
    return repo.save(p);
  }

  public List<Product> list() {
    return repo.findAll();
  }
}
