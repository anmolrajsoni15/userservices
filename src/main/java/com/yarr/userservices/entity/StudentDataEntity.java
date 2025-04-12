package com.yarr.userservices.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("student_data")
public class StudentDataEntity {

  @PrimaryKey
  private UUID id;

  private String student_id;
  private String institute_id;

  @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "form_data")
  private List<FormData> form_data;

  private String status;
  private Boolean is_continuing = true;
  private String session;
  private Instant created_at;
  private Instant updated_at;
  private UUID id_data_id;

  public StudentDataEntity(StudentData studentData) {
    this.id = UUID.randomUUID();
    this.student_id = studentData.getStudent_id();
    this.institute_id = studentData.getInstitute_id();
    this.form_data = studentData.getForm_data();
    this.status = studentData.getStatus();
    this.is_continuing = studentData.getIs_continuing();
    this.session = studentData.getSession();
    this.created_at = studentData.getCreated_at();
    this.updated_at = studentData.getUpdated_at();
  }

  // Convert to StudentData object (for backward compatibility)
  public StudentData toStudentData() {
    StudentData data = new StudentData();
    data.setStudent_id(this.student_id);
    data.setInstitute_id(this.institute_id);
    data.setForm_data(this.form_data);
    data.setStatus(this.status);
    data.setIs_continuing(this.is_continuing);
    data.setSession(this.session);
    data.setCreated_at(this.created_at);
    data.setUpdated_at(this.updated_at);
    return data;
  }
}
