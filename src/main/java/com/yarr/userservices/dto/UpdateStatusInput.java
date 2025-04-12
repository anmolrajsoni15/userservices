package com.yarr.userservices.dto;

import com.yarr.userservices.entity.OrderLogs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusInput {
  private OrderLogs orderLog;
  private String status_view;
}
