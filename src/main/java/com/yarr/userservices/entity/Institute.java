package com.yarr.userservices.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "institute")
@Data
public class Institute {
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  private String instituteName;
  private String instituteCode;
  private String instituteEmail;
  private String institutePhone;
  private String instituteAddress;
  private String instituteCity;
  private String instituteState;
  private String instituteCountry;
  private String instituteZip;
  private String instituteType;
  private String instituteWebsite;
  @Lob
  private String instituteLogo;
  private String instituteDescription;
  private String instituteApproval;

  public Institute() {
  }

}
