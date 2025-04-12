package com.yarr.userservices.dto;

import java.util.List;

import com.yarr.userservices.entity.FormData;
import com.yarr.userservices.entity.OrderLogs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveTemplate {
  private String status;
  private List<FormData> fields;
  private OrderLogs order_logs;
  private String status_view;
}
