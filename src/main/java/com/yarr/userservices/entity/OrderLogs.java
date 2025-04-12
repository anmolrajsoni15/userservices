package com.yarr.userservices.entity;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;
import java.util.List;
import java.time.Instant;

import lombok.Data;

@Data
@UserDefinedType("order_logs")
public class OrderLogs {
  private String status;
  private Instant date_time;
  private String usermail;
  private List<String> userrole;

  public OrderLogs() {
  }

  public OrderLogs(String status, Instant date_time, String usermail, List<String> userrole) {
    this.status = status;
    this.date_time = date_time;
    this.usermail = usermail;
    this.userrole = userrole;
  }
}
