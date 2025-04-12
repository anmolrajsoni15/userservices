package com.yarr.userservices.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.yarr.userservices.entity.Product;

@Repository
public interface ProductRepository extends CassandraRepository<Product, UUID> {

  @Query("SELECT * FROM product WHERE name LIKE ?0 ALLOW FILTERING")
  List<Product> findByNameContaining(String name);

  @Query("SELECT * FROM product WHERE created_by = ?0 ALLOW FILTERING")
  List<Product> findByCreatedBy(String createdBy);
}