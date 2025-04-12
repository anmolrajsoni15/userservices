package com.yarr.userservices.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.json.JSONObject;

import com.yarr.userservices.client.AdminServiceClient;
import com.yarr.userservices.dto.DashboardData;
import com.yarr.userservices.dto.IdDataResponse;
import com.yarr.userservices.dto.IdDataWithoutStudentRecord;
import com.yarr.userservices.dto.RegistrationRequest;
import com.yarr.userservices.dto.StudentDetails;
import com.yarr.userservices.dto.UserInput;
import com.yarr.userservices.dto.UpdateStudentStatusResponse;
import com.yarr.userservices.entity.FormData;
import com.yarr.userservices.entity.IdData;
import com.yarr.userservices.entity.Institute;
import com.yarr.userservices.entity.OrderLogs;
import com.yarr.userservices.entity.Orders;
import com.yarr.userservices.entity.StudentData;
import com.yarr.userservices.entity.StudentDataEntity;
import com.yarr.userservices.entity.UserFormRequest;
import com.yarr.userservices.repository.IdDataRepository;
import com.yarr.userservices.repository.InstituteRepository;
import com.yarr.userservices.repository.OrderRepository;
import com.yarr.userservices.repository.StudentDataRepository;

@Service
public class IdService {
  private final IdDataRepository idDataRepository;
  private final InstituteRepository instituteRepository;
  private final OrderRepository orderRepository;
  private final AdminServiceClient adminServiceClient;
  private final StudentDataRepository studentDataRepository;

  public IdService(IdDataRepository idDataRepository, InstituteRepository instituteRepository,
      OrderRepository orderRepository, AdminServiceClient adminServiceClient,
      StudentDataRepository studentDataRepository) {
    this.idDataRepository = idDataRepository;
    this.instituteRepository = instituteRepository;
    this.orderRepository = orderRepository;
    this.adminServiceClient = adminServiceClient;
    this.studentDataRepository = studentDataRepository;
  }

  // Helper method to convert IdData to IdDataResponse with complete student data
  private IdDataResponse convertToIdDataResponse(IdData idData) {
    if (idData == null) {
      return null;
    }
    List<StudentData> studentDataList = new ArrayList<>();
    if (idData.getStudent_ids() != null && !idData.getStudent_ids().isEmpty()) {
      List<StudentDataEntity> entities = studentDataRepository.findByIdDataId(idData.getId());
      studentDataList = entities.stream()
          .map(StudentDataEntity::toStudentData)
          .collect(Collectors.toList());
    }
    return new IdDataResponse(idData, studentDataList);
  }

  // Helper method to convert a list of IdData to a list of IdDataResponse
  private List<IdDataResponse> convertToIdDataResponseList(List<IdData> idDataList) {
    return idDataList.stream()
        .map(this::convertToIdDataResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public String getInstituteEmailByInstituteId(String instituteId) {
    Institute institute = instituteRepository.findByInstituteCode(instituteId);
    return institute != null ? institute.getInstituteEmail() : null;
  }

  @Transactional(readOnly = true)
  public Institute getInstituteByCode(String instituteCode) {
    return instituteRepository.findByInstituteCode(instituteCode);
  }

  public List<IdDataResponse> getAllIdData() {
    return convertToIdDataResponseList(idDataRepository.findAll());
  }

  public List<IdData> getAllIdDataExceptStudentData() {
    return idDataRepository.findAllIdDataExceptStudentData();
  }

  public List<IdData> getAllIdDataExceptStudentForAInstitute(String instituteId) {
    return idDataRepository.findIdDataByIdExceptStudentDataForAInstitute(instituteId);
  }

  public IdData getIdDataByIdExceptStudentData(UUID id) {
    return idDataRepository.findIdDataByIdExceptStudentData(id);
  }

  public IdDataResponse getIdDataById(UUID id) {
    IdData idData = idDataRepository.findById(id).orElse(null);
    return convertToIdDataResponse(idData);
  }

  public void applyIdFormDataItems(UUID id, List<FormData> formData) {
    IdData idData = idDataRepository.findById(id).orElse(null);
    if (idData != null) {
      idData.setForm_data(formData);
      idDataRepository.save(idData);
    }
  }

  // * keep this method as it is
  public IdDataWithoutStudentRecord createFormRequest(List<UserFormRequest> userFormRequests, UUID id) {
    IdData idData = idDataRepository.findById(id).orElse(null);
    if (idData != null) {
      List<UserFormRequest> userFormRequest = idData.getUser_form_request();
      if (userFormRequest == null) {
        userFormRequest = new ArrayList<>();
      } else {
        userFormRequest.clear();
      }
      userFormRequest.addAll(userFormRequests);
      idData.setUser_form_request(userFormRequest);
      idDataRepository.save(idData);

      IdDataWithoutStudentRecord idDataWithoutStudentRecord = new IdDataWithoutStudentRecord(
          idData.getId(),
          idData.getInstitute_id(),
          idData.getForm_data(),
          idData.getUser_form_request(),
          idData.getSession(),
          idData.getCreated_at());
      return idDataWithoutStudentRecord;
    }
    return null;
  }

  public IdDataResponse generateNewIdData(String instituteId, String session) {
    IdData existingData = idDataRepository.findByInstituteIdAndSession(instituteId, session);
    if (existingData != null) {
      return convertToIdDataResponse(existingData);
    }

    IdData idData = new IdData();
    idData.setId(UUID.randomUUID());
    idData.setInstitute_id(instituteId);
    idData.setSession(session);
    idData.setCreated_at(Instant.now());
    idData.setStudent_ids(new ArrayList<>());

    idDataRepository.save(idData);
    return convertToIdDataResponse(idData);
  }

  public String registerNewStudent(RegistrationRequest request) {
    try {
      String response = adminServiceClient.register(request);
      return response;
    } catch (Exception e) {
      return "Failed to add students";
    }
  }

  public IdDataResponse getAllIdDataWRTSession(String instituteId, String session) {
    IdData idData = idDataRepository.findByInstituteIdAndSession(instituteId, session);
    return convertToIdDataResponse(idData);
  }

  public List<String> getAllUniqueSessionValues(String instituteId) {
    List<IdData> sessionValues = idDataRepository.findDistinctSessionByInstituteId(instituteId);
    List<String> uniqueSessions = new ArrayList<>();
    for (IdData idData : sessionValues) {
      uniqueSessions.add(idData.getSession());
    }
    uniqueSessions.sort(
        (session1, session2) -> session2.compareTo(session1));
    return uniqueSessions;
  }

  public List<StudentDetails> getStudentDetails(String studentId) {
    String instituteId = adminServiceClient.getUserProvider(studentId);
    List<IdData> idDataList = idDataRepository.findByInstituteId(instituteId);
    List<StudentDetails> studentDetailsList = new ArrayList<>();

    for (IdData data : idDataList) {
      List<Orders> orders = orderRepository.findByProductReferenceAndProductType(data.getId().toString(), "idcard");
      if (orders == null) {
        continue;
      }
      orders.removeIf(
          O -> O.getOrder_logs().stream().anyMatch(orderLog -> orderLog.getStatus().equals("CANCELED-ORDER")));
      if (orders.isEmpty()) {
        continue;
      }
      Orders order = orders.get(0);
      List<OrderLogs> orderLogs = order.getOrder_logs();
      boolean isRequested = orderLogs.stream()
          .anyMatch(orderLog -> orderLog.getStatus().equals("REQUESTED-APPLICATION"));
      if (!isRequested) {
        continue;
      }

      // Check if student exists in the student data table
      StudentDataEntity studentDataEntity = studentDataRepository.findByStudentIdAndIdDataId(studentId, data.getId());
      StudentData studentData = studentDataEntity != null ? studentDataEntity.toStudentData() : null;

      if (studentData != null) {
        StudentDetails studentDetails = new StudentDetails(order.getProduct_id(), studentData);
        studentDetailsList.add(studentDetails);
      } else {
        // Create new student data if it doesn't exist
        StudentDataEntity newStudentDataEntity = new StudentDataEntity();
        newStudentDataEntity.setId(UUID.randomUUID());
        newStudentDataEntity.setStudent_id(studentId);
        newStudentDataEntity.setInstitute_id(instituteId);
        newStudentDataEntity.setForm_data(data.getForm_data());
        newStudentDataEntity.setStatus("PENDING");
        newStudentDataEntity.setSession(data.getSession());
        newStudentDataEntity.setCreated_at(Instant.now());
        newStudentDataEntity.setUpdated_at(Instant.now());
        newStudentDataEntity.setId_data_id(data.getId());

        studentDataRepository.save(newStudentDataEntity);

        if (data.getStudent_ids() == null) {
          List<String> studentIds = new ArrayList<>();
          studentIds.add(studentId);
          data.setStudent_ids(studentIds);
          idDataRepository.save(data);
        } else if (!data.getStudent_ids().contains(studentId)) {
          data.getStudent_ids().add(studentId);
          idDataRepository.save(data);
        }

        StudentDetails studentDetails = new StudentDetails(order.getProduct_id(), newStudentDataEntity.toStudentData());
        studentDetailsList.add(studentDetails);
      }
    }
    return studentDetailsList;
  }

  public String fillIdCardData(List<UserInput> userInput, String studentId, String instituteId, String session) {
    IdData idData = idDataRepository.findByInstituteIdAndSession(instituteId, session);
    if (idData != null) {
      StudentDataEntity studentDataEntity = studentDataRepository.findByStudentIdAndIdDataId(studentId, idData.getId());

      if (studentDataEntity != null) {
        List<FormData> studentFormData = studentDataEntity.getForm_data();
        for (UserInput input : userInput) {
          studentFormData.stream()
              .filter(formData -> formData.getId().equals(input.getId()))
              .forEach(formData -> formData.setValue(input.getValue()));
        }
        studentDataEntity.setForm_data(studentFormData);
        studentDataEntity.setStatus("SUBMITTED");
        studentDataEntity.setUpdated_at(Instant.now());
        studentDataRepository.save(studentDataEntity);
        return "Data updated successfully";
      }
    }
    return "Data not found";
  }

  public String checkIfStudentHaveFilledIdCardDetails(String studentId, String instituteId) {
    if (studentId == null || instituteId == null) {
      return "Invalid input parameters";
    }

    List<IdData> idDataList = idDataRepository.findByInstituteId(instituteId);

    if (idDataList == null || idDataList.isEmpty()) {
      return "No data found for institute";
    }

    idDataList.sort((idData1, idData2) -> idData2.getSession().compareTo(idData1.getSession()));

    IdData currSessionIdData = idDataList.get(0);
    if (currSessionIdData == null) {
      return "Session data not found";
    }

    // Check if student exists in student data table
    StudentDataEntity studentDataEntity = studentDataRepository.findByStudentIdAndIdDataId(studentId,
        currSessionIdData.getId());
    if (studentDataEntity == null) {
      return "No student data found";
    }

    List<FormData> formData = studentDataEntity.getForm_data();
    if (formData == null || formData.isEmpty()) {
      return "Form data not found";
    }

    for (FormData data : formData) {
      if (data == null || data.getValue() == null || data.getValue().isEmpty()) {
        return "INCOMPLETE";
      }
    }
    return "COMPLETE";
  }

  public String deleteIdData(UUID id) {
    IdData idData = idDataRepository.findById(id).orElse(null);
    if (idData != null) {
      // Delete associated student data first
      List<StudentDataEntity> studentDataEntities = studentDataRepository.findByIdDataId(id);
      if (studentDataEntities != null && !studentDataEntities.isEmpty()) {
        studentDataRepository.deleteAll(studentDataEntities);
      }
      idDataRepository.delete(idData);
      return "Data deleted successfully";
    }
    return "Data not found";
  }

  public DashboardData getAllRequiredDashboardData(String instituteId, String session) {
    IdData idData = idDataRepository.findByInstituteIdAndSession(instituteId, session);
    if (idData != null) {
      List<StudentDataEntity> studentDataEntities = studentDataRepository.findByIdDataId(idData.getId());
      Number totalStudents = studentDataEntities.size();
      Number pendingApplications = studentDataEntities.stream()
          .filter(data -> "PENDING".equals(data.getStatus()))
          .count();
      Number submittedApplications = studentDataEntities.stream()
          .filter(data -> "SUBMITTED".equals(data.getStatus()) || "RECEIVED".equals(data.getStatus()))
          .count();
      Number receivedId = studentDataEntities.stream()
          .filter(data -> "RECEIVED".equals(data.getStatus()))
          .count();

      JSONObject batchWiseData = new JSONObject();
      JSONObject branchWiseData = new JSONObject();

      for (StudentDataEntity data : studentDataEntities) {
        List<FormData> formDataList = data.getForm_data();
        String batch = null;
        String branch = null;

        // Extract batch and branch from form data
        for (FormData formData : formDataList) {
          if ("Batch".equals(formData.getKey())) {
            batch = formData.getValue();
          } else if ("Branch".equals(formData.getKey())) {
            branch = formData.getValue();
          }
        }

        if (batch == null || branch == null) {
          continue;
        }

        String status = data.getStatus();

        // Initialize batch data if not present
        if (!batchWiseData.has(batch)) {
          JSONObject batchData = new JSONObject();
          batchData.put("pending", 0);
          batchData.put("submitted", 0);
          batchData.put("received", 0);
          batchWiseData.put(batch, batchData);
        }

        // Initialize branch data if not present
        if (!branchWiseData.has(branch)) {
          JSONObject branchData = new JSONObject();
          branchData.put("pending", 0);
          branchData.put("submitted", 0);
          branchData.put("received", 0);
          branchWiseData.put(branch, branchData);
        }

        // Update batch data
        JSONObject batchData = batchWiseData.getJSONObject(batch);
        if (status.equals("PENDING")) {
          batchData.put("pending", batchData.getInt("pending") + 1);
        } else if (status.equals("SUBMITTED") || status.equals("RECEIVED")) {
          batchData.put("submitted", batchData.getInt("submitted") + 1);
          if (status.equals("RECEIVED")) {
            batchData.put("received", batchData.getInt("received") + 1);
          }
        }

        // Update branch data
        JSONObject branchData = branchWiseData.getJSONObject(branch);
        if (status.equals("PENDING")) {
          branchData.put("pending", branchData.getInt("pending") + 1);
        } else if (status.equals("SUBMITTED") || status.equals("RECEIVED")) {
          branchData.put("submitted", branchData.getInt("submitted") + 1);
          if (status.equals("RECEIVED")) {
            branchData.put("received", branchData.getInt("received") + 1);
          }
        }
      }

      DashboardData dashboardData = new DashboardData(totalStudents, submittedApplications, pendingApplications,
          receivedId, batchWiseData.toString(), branchWiseData.toString());
      return dashboardData;
    }
    return null;
  }

  public String checkFormRequestStatus(String instituteId) {
    // Get all IdData for the institute
    List<IdData> idDataList = idDataRepository.findByInstituteId(instituteId);

    if (idDataList == null || idDataList.isEmpty()) {
      return "INSTITUTE_NOT_FOUND";
    }

    // Sort idDataList by session in descending order
    idDataList.sort((idData1, idData2) -> idData2.getSession().compareTo(idData1.getSession()));

    // Only use the first (most recent) IdData entry
    IdData latestIdData = idDataList.get(0);
    String latestSession = latestIdData.getSession();

    if(latestIdData != null && latestIdData.getIs_form_closed() != null && latestIdData.getForm_closing_time() != null && (latestIdData.getIs_form_closed() == true || latestIdData.getForm_closing_time().isBefore(Instant.now()))) {
      return "APPLICATION_CLOSED";
    }

    // Check if form_data exists and is not empty
    if (latestIdData.getForm_data() != null && !latestIdData.getForm_data().isEmpty()) {
      // Find orders with matching product_reference
      List<Orders> orders = orderRepository.findByProductReferenceAndProductType(
          latestIdData.getId().toString(), "idcard");

      if (orders != null && !orders.isEmpty()) {
        // Filter out cancelled orders
        orders.removeIf(order -> order.getOrder_logs().stream()
            .anyMatch(log -> "ORDER-CANCELED".equals(log.getStatus())));

        // Check remaining orders for REQUESTED-APPLICATION status and matching session
        for (Orders order : orders) {
          if (order.getOrder_logs().stream()
              .anyMatch(log -> "REQUESTED-APPLICATION".equals(log.getStatus())) &&
              latestSession.equals(order.getSession())) {
            return "APPLICATION_FOUND";
          }
        }
      }
    }

    return "APPLICATION_NOT_FOUND";
  }

  public IdDataResponse openApplicationWindow(String instituteId, String session, Instant closingTime) {
    IdData idData = idDataRepository.findByInstituteIdAndSession(instituteId, session);
    if (idData != null) {
      idData.setForm_closing_time(closingTime);
      idData.setIs_form_closed(false);
      idDataRepository.save(idData);
      return convertToIdDataResponse(idData);
    } else {
      return null;
    }
  }

  public IdDataResponse closeApplicationWindow(String instituteId, String session) {
    IdData idData = idDataRepository.findByInstituteIdAndSession(instituteId, session);
    if (idData != null) {
      idData.setIs_form_closed(true);
      idDataRepository.save(idData);
      return convertToIdDataResponse(idData);
    } else {
      return null;
    }
  }

  public UpdateStudentStatusResponse updateStudentStatus(UUID idDataId, List<String> studentIds, String status) {
    UpdateStudentStatusResponse response = new UpdateStudentStatusResponse();
    Map<String, String> updatedStudents = new HashMap<>();
    int successCount = 0;
    int failCount = 0;

    // Find all student data entities with the given IdData ID
    List<StudentDataEntity> studentDataEntities = studentDataRepository.findByIdDataId(idDataId);

    // Filter to only those in the requested list
    List<StudentDataEntity> toUpdate = studentDataEntities.stream()
        .filter(entity -> studentIds.contains(entity.getStudent_id()))
        .collect(Collectors.toList());

    for (StudentDataEntity entity : toUpdate) {
      try {
        entity.setStatus(status);
        entity.setUpdated_at(Instant.now());
        studentDataRepository.save(entity);
        updatedStudents.put(entity.getStudent_id(), status);
        successCount++;
      } catch (Exception e) {
        updatedStudents.put(entity.getStudent_id(), "UPDATE_FAILED");
        failCount++;
      }
    }

    response.setUpdatedStudents(updatedStudents);
    response.setTotalUpdated(successCount);
    response.setTotalFailed(failCount);
    response.setMessage(successCount + " students updated successfully, " + failCount + " failed");

    return response;
  }
}
