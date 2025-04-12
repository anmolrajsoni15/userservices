package com.yarr.userservices.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentStatusRequest {
  private List<String> studentIds;
  private String status;
}
