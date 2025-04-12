package com.yarr.userservices.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.yarr.userservices.entity.FormData;
import com.yarr.userservices.entity.IdData;
import com.yarr.userservices.entity.StudentData;
import com.yarr.userservices.entity.UserFormRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IdDataResponse {
  private UUID id;
  private String institute_id;
  private List<FormData> form_data;
  private List<StudentData> student_data;
  private List<UserFormRequest> user_form_request;
  private Instant form_closing_time;
  private Boolean is_form_closed;
  private String session;
  private Instant created_at;

  public IdDataResponse(IdData idData, List<StudentData> studentData) {
    this.id = idData.getId();
    this.institute_id = idData.getInstitute_id();
    this.form_data = idData.getForm_data();
    this.student_data = studentData;
    this.user_form_request = idData.getUser_form_request();
    this.form_closing_time = idData.getForm_closing_time();
    this.is_form_closed = idData.getIs_form_closed();
    this.session = idData.getSession();
    this.created_at = idData.getCreated_at();
  }
}
