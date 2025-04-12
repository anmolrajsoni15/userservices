package com.yarr.userservices.entity;

import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.Data;

@Data
@UserDefinedType("user_form_request")
public class UserFormRequest {
  private String label;
  private String description;
  private String type;
  private String side;
  private Instant modification_date;
}
