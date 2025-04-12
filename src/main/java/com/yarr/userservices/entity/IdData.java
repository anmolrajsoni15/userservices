package com.yarr.userservices.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

import org.springframework.data.cassandra.core.mapping.CassandraType;

@Data
@Table("id_data")
public class IdData {

  @PrimaryKey
  private UUID id;

  private String institute_id;

  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "form_data")
  private List<FormData> form_data;

  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
  private List<String> student_ids = new ArrayList<>();

  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "user_form_request")
  private List<UserFormRequest> user_form_request = new ArrayList<>();

  private Instant form_closing_time;
  private Boolean is_form_closed;
  private String session;
  private Instant created_at;
}