package com.yarr.userservices.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Orders {

  @PrimaryKey
  private UUID id;

  private String user_id;
  private String product_id;
  private String template_id;
  private String product_type;
  private String product_reference;
  private String status_view;
  // Change from Number to Integer to fix the mapping error
  private Integer quantity;
  private Float total_price;
  private String session;
  private String status;
  private Instant order_date;
  private Float service_charge;
  private Float tax;
  private String payment_mode;

  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "query_logs")
  private List<QueryLogs> query_logs = new ArrayList<>();

  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "order_logs")
  private List<OrderLogs> order_logs = new ArrayList<>();

  private Instant last_updated;
}
