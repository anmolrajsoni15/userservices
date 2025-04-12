package com.yarr.userservices.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.yarr.userservices.entity.Orders;

@Repository
public interface OrderRepository extends CassandraRepository<Orders, UUID>{

  @Query("SELECT * FROM orders WHERE user_id=?0 ALLOW FILTERING")
  List<Orders> findAllByUserId(String userId);

  @Query("SELECT * FROM orders WHERE product_reference=?0 AND product_type=?1 ALLOW FILTERING")
  List<Orders> findByProductReferenceAndProductType(String productReference, String productType);
}
