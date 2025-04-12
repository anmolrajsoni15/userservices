package com.yarr.userservices.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yarr.userservices.dto.RegistrationRequest;
import com.yarr.userservices.dto.Template;

@FeignClient(name = "ADMINSERVICES", url = "${admin.url}", configuration = FeignClientConfiguration.class)
public interface AdminServiceClient {
  
  @PostMapping("/admin/institute/student/register")
  String register(@RequestBody RegistrationRequest request);

  @GetMapping("/admin/institute/student/{email}")
  String getStudentId(@PathVariable String email);

  @GetMapping("/admin/student/{id}/provider")
  String getUserProvider(@PathVariable String id);

  @DeleteMapping("/admin/user/{id}")
  String removeUser(@PathVariable UUID id); 

  @GetMapping("/template/{id}")
  Template getTemplateById(@PathVariable String id);
}
