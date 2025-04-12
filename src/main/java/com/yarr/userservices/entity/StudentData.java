package com.yarr.userservices.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.Data;

@Data
@UserDefinedType("student_data")
public class StudentData {
  private String student_id;
  private String institute_id;

  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "form_data")
  private List<FormData> form_data;
  
  private String status;
  private Boolean is_continuing = true;
  private String session;
  private Instant created_at;
  private Instant updated_at;

  public StudentData() {
  }

  public StudentData(String studentId, String instituteId, List<FormData> formData, String status, String session, Instant updated_at) {
    this.student_id = studentId;
    this.institute_id = instituteId;
    this.form_data = formData;
    this.status = status;
    this.is_continuing = true;
    this.session = session;
    this.created_at = Instant.now();
    this.updated_at = updated_at;
  }
}
