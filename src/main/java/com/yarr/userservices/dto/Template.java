package com.yarr.userservices.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template {
  private String id;
  private String name;
  private String dimensions;
  private String orientation;
  private String tag;
  private String front_url;
  private String back_url;
  private Instant created_at;
  private Instant updated_at;
}
