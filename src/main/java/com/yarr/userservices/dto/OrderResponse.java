package com.yarr.userservices.dto;

import java.util.List;

import com.yarr.userservices.entity.Orders;

import lombok.Data;

@Data
public class OrderResponse {
  private Orders order;
  private List<Orders> orders;
  private String status;
  private String message;
  private Object data;

  public OrderResponse(Orders order, List<Orders> orders, String status, String message) {
    this.order = order;
    this.orders = orders;
    this.status = status;
    this.message = message;
  }

  public OrderResponse(Orders order, List<Orders> orders, String status, String message, Object data) {
    this.order = order;
    this.orders = orders;
    this.status = status;
    this.message = message;
    this.data = data;
  }
}
