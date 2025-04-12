package com.yarr.userservices.converters;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.yarr.userservices.entity.StudentData;
import com.yarr.userservices.entity.StudentDataEntity;

@Component
public class StudentDataConverter {

  /**
   * Convert a StudentDataEntity to a StudentData object for backward
   * compatibility
   */
  public StudentData toStudentData(StudentDataEntity entity) {
    if (entity == null) {
      return null;
    }

    StudentData data = new StudentData();
    data.setStudent_id(entity.getStudent_id());
    data.setInstitute_id(entity.getInstitute_id());
    data.setForm_data(entity.getForm_data());
    data.setStatus(entity.getStatus());
    data.setIs_continuing(entity.getIs_continuing());
    data.setSession(entity.getSession());
    data.setCreated_at(entity.getCreated_at());
    data.setUpdated_at(entity.getUpdated_at());
    return data;
  }

  /**
   * Convert a list of StudentDataEntity to a list of StudentData
   */
  public List<StudentData> toStudentDataList(List<StudentDataEntity> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream()
        .map(this::toStudentData)
        .collect(Collectors.toList());
  }

  /**
   * Create a new StudentDataEntity from a StudentData object
   */
  public StudentDataEntity toStudentDataEntity(StudentData studentData, UUID idDataId) {
    if (studentData == null) {
      return null;
    }

    StudentDataEntity entity = new StudentDataEntity();
    entity.setId(UUID.randomUUID());
    entity.setStudent_id(studentData.getStudent_id());
    entity.setInstitute_id(studentData.getInstitute_id());
    entity.setForm_data(studentData.getForm_data());
    entity.setStatus(studentData.getStatus());
    entity.setIs_continuing(studentData.getIs_continuing());
    entity.setSession(studentData.getSession());
    entity.setCreated_at(studentData.getCreated_at());
    entity.setUpdated_at(studentData.getUpdated_at());
    entity.setId_data_id(idDataId);

    return entity;
  }

  /**
   * Convert a list of StudentData to a list of StudentDataEntity
   */
  public List<StudentDataEntity> toStudentDataEntityList(List<StudentData> studentDataList, UUID idDataId) {
    if (studentDataList == null) {
      return null;
    }
    return studentDataList.stream()
        .map(data -> toStudentDataEntity(data, idDataId))
        .collect(Collectors.toList());
  }
}
