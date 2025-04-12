package com.yarr.userservices.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class StudentData {
  private UUID id;
  private String instituteId;
  private String formData;
  private String status;
  private Instant createdAt;

  public StudentData() {
  }

  public StudentData(UUID id, String instituteId, String formData, String status, Instant createdAt) {
    this.id = id;
    this.instituteId = instituteId;
    this.formData = formData;
    this.status = status;
    this.createdAt = createdAt;
  }
}
