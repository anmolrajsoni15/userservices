package com.yarr.userservices.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.yarr.userservices.entity.StudentDataEntity;

@Repository
public interface StudentDataRepository extends CassandraRepository<StudentDataEntity, UUID> {

  @Query("SELECT * FROM student_data WHERE student_id = ?0 ALLOW FILTERING")
  List<StudentDataEntity> findByStudentId(String studentId);

  @Query("SELECT * FROM student_data WHERE institute_id = ?0 AND session = ?1 ALLOW FILTERING")
  List<StudentDataEntity> findByInstituteIdAndSession(String instituteId, String session);

  @Query("SELECT * FROM student_data WHERE id_data_id = ?0 ALLOW FILTERING")
  List<StudentDataEntity> findByIdDataId(UUID idDataId);

  @Query("SELECT * FROM student_data WHERE student_id = ?0 AND institute_id = ?1 AND session = ?2 ALLOW FILTERING")
  StudentDataEntity findByStudentIdAndInstituteIdAndSession(String studentId, String instituteId, String session);

  @Query("SELECT * FROM student_data WHERE student_id = ?0 AND id_data_id = ?1 ALLOW FILTERING")
  StudentDataEntity findByStudentIdAndIdDataId(String studentId, UUID idDataId);

  @Query("SELECT * FROM student_data WHERE student_id = ?0 AND institute_id = ?1 ALLOW FILTERING")
  List<StudentDataEntity> findByStudentIdAndInstituteId(String studentId, String instituteId);
}
