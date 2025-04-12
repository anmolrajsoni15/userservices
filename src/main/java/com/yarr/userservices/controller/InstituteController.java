package com.yarr.userservices.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yarr.userservices.entity.Institute;
import com.yarr.userservices.jwt.JwtUtil;
import com.yarr.userservices.services.IdService;
import com.yarr.userservices.services.InstituteService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/institute")
public class InstituteController {

  @Autowired
  private InstituteService instituteService;

  @Autowired
  private IdService idService;

  @Autowired
  private JwtUtil jwtUtil;

  @PostMapping("/register")
  public ResponseEntity<Institute> forRegisterInstitute(@RequestBody Institute institute) {
    return ResponseEntity.ok(instituteService.registerNewInstitute(institute));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @GetMapping("/all")
  public ResponseEntity<Iterable<Institute>> forGetAllInstitutes() {
    return ResponseEntity.ok(instituteService.getAllInstitutes());
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN') or hasRole('INSTITUTE') or hasRole('STUDENT')")
  @GetMapping("/{id}")
  public ResponseEntity<Institute> getInstituteByCode(@PathVariable String id) {
    return ResponseEntity.ok(instituteService.getInstituteByCode(id));
  }

  @GetMapping("/me")
  public ResponseEntity<?> forGetInstitute(HttpServletRequest request) {
    String jwt = request.getHeader("Authorization");
    if (!StringUtils.hasText(jwt) || !jwt.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().body("Authorization token is missing");
    }
    String email = jwtUtil.extractUsername(jwt.substring(7));

    Institute institute = instituteService.getInstituteByEmail(email);
    return ResponseEntity.ok(institute);
  }

  @GetMapping("/{instituteId}/allUniqueSessions")
  public ResponseEntity<?> forGetAllUniqueSessions(@PathVariable String instituteId) {
    return ResponseEntity.ok(idService.getAllUniqueSessionValues(instituteId));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PostMapping("/{instituteId}/approve")
  public ResponseEntity<Institute> forApproveInstitute(@PathVariable UUID instituteId) {
    return ResponseEntity.ok(instituteService.approveInstituteRegistration(instituteId));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PostMapping("/{instituteId}/reject")
  public ResponseEntity<String> forRejectInstitute(@PathVariable UUID instituteId) {
    return ResponseEntity.ok(instituteService.rejectInstituteRegistration(instituteId));
  }

}
