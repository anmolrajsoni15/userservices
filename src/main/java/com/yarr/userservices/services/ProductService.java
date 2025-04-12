package com.yarr.userservices.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.yarr.userservices.entity.Product;
import com.yarr.userservices.repository.ProductRepository;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Product getProductById(UUID id) {
    return productRepository.findById(id).orElse(null);
  }

  public List<Product> searchProductsByName(String name) {
    return productRepository.findByNameContaining(name);
  }

  public List<Product> getProductsByCreatedBy(String createdBy) {
    return productRepository.findByCreatedBy(createdBy);
  }

  public Product createProduct(Product product) {
    product.setId(UUID.randomUUID());
    product.setCreated_at(Instant.now());
    product.setUpdated_at(Instant.now());
    return productRepository.save(product);
  }

  public Product updateProduct(UUID id, Product productDetails) {
    Product existingProduct = productRepository.findById(id).orElse(null);
    if (existingProduct == null) {
      return null;
    }

    existingProduct.setName(productDetails.getName());
    existingProduct.setDescription(productDetails.getDescription());
    existingProduct.setPhoto(productDetails.getPhoto());
    existingProduct.setPrice(productDetails.getPrice());
    existingProduct.setDiscount(productDetails.getDiscount());
    existingProduct.setUpdated_at(Instant.now());

    return productRepository.save(existingProduct);
  }

  public boolean deleteProduct(UUID id) {
    try {
      Product product = productRepository.findById(id).orElse(null);
      if (product == null) {
        return false;
      }

      productRepository.delete(product);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}