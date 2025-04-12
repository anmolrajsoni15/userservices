package com.yarr.userservices.dto;

import com.yarr.userservices.entity.StudentData;

import lombok.Data;

@Data
public class StudentDetails {
  private String template_id;
  private StudentData student_data;

  public StudentDetails() {
  }

  public StudentDetails(String templateId, StudentData studentData) {
    this.template_id = templateId;
    this.student_data = studentData;
  }
}
