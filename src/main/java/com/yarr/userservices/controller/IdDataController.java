package com.yarr.userservices.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yarr.userservices.dto.ClosingTime;
import com.yarr.userservices.dto.IdDataResponse;
import com.yarr.userservices.dto.IdDataWithoutStudentRecord;
import com.yarr.userservices.dto.SaveTemplate;
import com.yarr.userservices.dto.UpdateStudentStatusRequest;
import com.yarr.userservices.dto.UpdateStudentStatusResponse;
import com.yarr.userservices.dto.UserInput;
import com.yarr.userservices.entity.FormData;
import com.yarr.userservices.entity.IdData;
import com.yarr.userservices.entity.OrderLogs;
import com.yarr.userservices.entity.Orders;
import com.yarr.userservices.entity.UserFormRequest;
import com.yarr.userservices.services.IdService;
import com.yarr.userservices.services.OrdersService;

@RestController
@RequestMapping("/id-data")
public class IdDataController {

  @Autowired
  private IdService idService;

  @Autowired
  private OrdersService ordersService;

  @PreAuthorize("hasRole('ADMIN') or hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getIdDataById(@PathVariable String id) {
    return ResponseEntity.ok(idService.getIdDataById(UUID.fromString(id)));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @GetMapping("/{id}/preview")
  public ResponseEntity<?> getIdDataByIdPreview(@PathVariable String id) {
    return ResponseEntity.ok(idService.getIdDataByIdExceptStudentData(UUID.fromString(id)));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @PostMapping("/upload-id-data")
  public ResponseEntity<?> uploadIdData(@RequestParam("instituteId") String instituteId,
      @RequestParam("session") String session) {
    return ResponseEntity.ok(idService.generateNewIdData(instituteId, session));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @GetMapping("/{instituteId}/{session}")
  public ResponseEntity<?> getIdData(@PathVariable String instituteId, @PathVariable String session) {
    return ResponseEntity.ok(idService.getAllIdDataWRTSession(instituteId, session));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN') or hasRole('SUPERADMIN')")
  @GetMapping("/all")
  public ResponseEntity<?> getAllIdData() {
    return ResponseEntity.ok(idService.getAllIdData());
  }

  @PreAuthorize("hasRole('INSTITUTE')")
  @GetMapping("/all-orders/{instituteId}")
  public ResponseEntity<List<IdData>> getAllIdDataofInstitute(@PathVariable String instituteId) {
    return ResponseEntity.ok(idService.getAllIdDataExceptStudentForAInstitute(instituteId));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @GetMapping("/all-preview")
  public ResponseEntity<List<IdData>> getAllIdDataPreview() {
    return ResponseEntity.ok(idService.getAllIdDataExceptStudentData());
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @GetMapping("/dashboard/{instituteId}/{session}")
  public ResponseEntity<?> getIdDataDashboard(@PathVariable String instituteId, @PathVariable String session) {
    return ResponseEntity.ok(idService.getAllRequiredDashboardData(instituteId, session));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PatchMapping("/apply-template-formatting/{id}/{orderId}")
  public ResponseEntity<String> applyTemplate(@PathVariable String id, @PathVariable String orderId,
      @RequestBody List<FormData> template) {
    idService.applyIdFormDataItems(UUID.fromString(id), template);
    ordersService.updateOnlyStatus(UUID.fromString(orderId), "SAVE-AS-DRAFT");
    return ResponseEntity.ok("Formatting applied successfully");
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PatchMapping("/complete-template-formatting/{id}/{orderId}")
  public ResponseEntity<Orders> saveTemplate(@PathVariable String id, @PathVariable String orderId,
      @RequestBody SaveTemplate template) {
    OrderLogs orderLogsData = template.getOrder_logs();
    idService.applyIdFormDataItems(UUID.fromString(id), template.getFields());

    return ResponseEntity
        .ok(ordersService.updateOrderStatus(UUID.fromString(orderId), orderLogsData, template.getStatus_view()));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PostMapping("/fill-required-fields/{id}")
  public ResponseEntity<IdDataWithoutStudentRecord> fillRequiredFields(
      @RequestBody List<UserFormRequest> userFormRequests, @PathVariable String id) {
    return ResponseEntity.ok(idService.createFormRequest(userFormRequests, UUID.fromString(id)));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteIdData(@PathVariable String id) {
    return ResponseEntity.ok(idService.deleteIdData(UUID.fromString(id)));
  }

  @GetMapping("/student/getAllStudentDetails/{studentId}")
  public ResponseEntity<?> getAllIdDataByStudentId(@PathVariable String studentId) {
    return ResponseEntity.ok(idService.getStudentDetails(studentId));
  }

  @PostMapping("/student/fillIdCardDetails/{studentId}/{instituteId}/{session}")
  public ResponseEntity<?> fillIdCardDetails(@RequestBody List<UserInput> idDetails, @PathVariable String studentId,
      @PathVariable String instituteId, @PathVariable String session) {
    return ResponseEntity.ok(idService.fillIdCardData(idDetails, studentId, instituteId, session));
  }

  @GetMapping("/check-form-request/{instituteId}")
  public ResponseEntity<String> checkFormRequestStatus(@PathVariable String instituteId) {
    return ResponseEntity.ok(idService.checkFormRequestStatus(instituteId));
  }

  @GetMapping("/check-student-status/{studentId}/{instituteId}")
  public ResponseEntity<String> checkIfStudentHaveFilledIdCardDetails(@PathVariable String studentId,
      @PathVariable String instituteId) {
    return ResponseEntity.ok(idService.checkIfStudentHaveFilledIdCardDetails(studentId, instituteId));
  }

  @PreAuthorize("hasRole('INSTITUTE')")
  @PatchMapping("/openApplicationFormTime/{instituteId}/{session}")
  public ResponseEntity<?> openApplicationFormTime(@PathVariable String instituteId, @PathVariable String session,
      @RequestBody ClosingTime closingTime) {
    return ResponseEntity.ok(idService.openApplicationWindow(instituteId, session, closingTime.getForm_closing_time()));
  }

  @PreAuthorize("hasRole('INSTITUTE')")
  @PatchMapping("/closeApplicationForm/{instituteId}/{session}")
  public ResponseEntity<?> closeApplicationFormTime(@PathVariable String instituteId, @PathVariable String session) {
    return ResponseEntity.ok(idService.closeApplicationWindow(instituteId, session));
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @PatchMapping("/update-student-status/{idDataId}")
  public ResponseEntity<UpdateStudentStatusResponse> updateStudentStatus(
      @PathVariable String idDataId,
      @RequestBody UpdateStudentStatusRequest request) {
    return ResponseEntity.ok(
        idService.updateStudentStatus(UUID.fromString(idDataId), request.getStudentIds(), request.getStatus()));
  }
}
