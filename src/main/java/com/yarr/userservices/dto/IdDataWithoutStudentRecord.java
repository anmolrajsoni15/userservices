package com.yarr.userservices.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.yarr.userservices.entity.FormData;
import com.yarr.userservices.entity.OrderLogs;
import com.yarr.userservices.entity.QueryLogs;
import com.yarr.userservices.entity.UserFormRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdDataWithoutStudentRecord {
  private UUID id;
  
  private String institute_id;
  // private String template_id;    // !no longer needed
  private List<FormData> form_data;
  
  // private List<QueryLogs> query_logs = new ArrayList<>();
  // private List<OrderLogs> order_logs = new ArrayList<>();
  private List<UserFormRequest> user_form_request = new ArrayList<>();
  
  private String session;
  // private String status;    // !no longer needed

  // private Instant order_date;   // !no longer needed
  private Instant created_at;

}
