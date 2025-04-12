package com.yarr.userservices.dto;

import lombok.Data;

@Data
public class DashboardData {
  private Number totalStudents;
  private Number submittedApplications;
  private Number pendingApplications;
  private Number receivedId;
  private String batchWiseDistribution;
  private String branchWiseDistribution;

  public DashboardData() {
  }

  public DashboardData(Number totalStudents, Number submittedApplications, Number pendingApplications, Number receivedId, String batchWiseDistribution, String branchWiseDistribution) {
    this.totalStudents = totalStudents;
    this.submittedApplications = submittedApplications;
    this.pendingApplications = pendingApplications;
    this.receivedId = receivedId;
    this.batchWiseDistribution = batchWiseDistribution;
    this.branchWiseDistribution = branchWiseDistribution;
  }
}
