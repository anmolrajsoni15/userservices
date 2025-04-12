package com.yarr.userservices.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yarr.userservices.entity.Product;
import com.yarr.userservices.services.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<List<Product>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    return ResponseEntity.ok(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
    Product product = productService.getProductById(id);
    if (product != null) {
      return ResponseEntity.ok(product);
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
    List<Product> products = productService.searchProductsByName(name);
    return ResponseEntity.ok(products);
  }

  @GetMapping("/creator/{createdBy}")
  public ResponseEntity<List<Product>> getProductsByCreator(@PathVariable String createdBy) {
    List<Product> products = productService.getProductsByCreatedBy(createdBy);
    return ResponseEntity.ok(products);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PostMapping
  public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product createdProduct = productService.createProduct(product);
    return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product product) {
    Product updatedProduct = productService.updateProduct(id, product);
    if (updatedProduct != null) {
      return ResponseEntity.ok(updatedProduct);
    }
    return ResponseEntity.notFound().build();
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
    boolean deleted = productService.deleteProduct(id);
    if (deleted) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}