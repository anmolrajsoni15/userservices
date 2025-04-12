package com.yarr.userservices.entity;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.Data;

@Data
@UserDefinedType("form_data")
public class FormData {
  private String id;
  private String key;
  private String label;
  private String type;
  private String side;
  private Boolean show_label;
  private String style;
  private String value;
  private String value_type;

  public FormData() {
  }

  public FormData(String id, String key, String label, String type, String side, Boolean show_label, String style, String value, String value_type) {
    this.id = id;
    this.key = key;
    this.label = label;
    this.type = type;
    this.side = side;
    this.show_label = show_label;
    this.style = style;
    this.value = value;
    this.value_type = value_type;
  }
}
