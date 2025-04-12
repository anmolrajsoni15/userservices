package com.yarr.userservices.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentStatusResponse {
  private Map<String, String> updatedStudents; // Map of student_id -> status
  private int totalUpdated;
  private int totalFailed;
  private String message;
}
