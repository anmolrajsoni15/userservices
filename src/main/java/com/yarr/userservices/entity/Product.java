package com.yarr.userservices.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class Product {

  @PrimaryKey
  private UUID id;

  private String name;
  private String description;
  private List<String> photo;
  private Instant created_at;
  private Instant updated_at;
  private String created_by;
  private Float price;
  private Float discount; // Changed from int to Float to match the schema definition
}