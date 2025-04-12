package com.yarr.userservices.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentOrderDetailsDTO {
  private UUID orderId;
  private String product_type;
  private String session;
  private List<OrderLogDTO> orderLogs;
  private ProductInfoDTO product;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderLogDTO {
    private String status;
    private Instant date_time;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ProductInfoDTO {
    private String name;
    private String description;
    private Float price;
    private Float discount;
    private String dimension;
    private String front_url;
    private String back_url;
    private Float service_charge;
    private Float tax;
  }
}
