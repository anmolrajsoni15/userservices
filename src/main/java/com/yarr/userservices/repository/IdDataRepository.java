package com.yarr.userservices.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.yarr.userservices.entity.IdData;

@Repository
public interface IdDataRepository extends CassandraRepository<IdData, UUID> {
  @Query("SELECT * FROM id_data WHERE institute_id=?0 AND session=?1 ALLOW FILTERING")
  IdData findByInstituteIdAndSession(String instituteId, String session);
  
  @Query("SELECT * FROM id_data WHERE institute_id=?0 AND session=?1 AND status=?2 ALLOW FILTERING")
  IdData findByInstituteIdAndSessionAndStatus(String instituteId, String session, String status);
  
  @Query("SELECT * FROM id_data WHERE institute_id=?0 ALLOW FILTERING")
  List<IdData> findByInstituteId(String instituteId);
  
  @Query("SELECT * FROM id_data WHERE institute_id=?0 AND status=?1 ALLOW FILTERING")
  List<IdData> findByInstituteIdAndStatus(String instituteId, String status);

  @Query("SELECT session FROM id_data WHERE institute_id=?0 ALLOW FILTERING")
  List<IdData> findDistinctSessionByInstituteId(String instituteId);

  @Query("SELECT id, institute_id, form_data, session, user_form_request, created_at FROM id_data")
  List<IdData> findAllIdDataExceptStudentData();

  @Query("SELECT id, institute_id, form_data, session, user_form_request, created_at FROM id_data WHERE institute_id = ?0 ALLOW FILTERING")
  List<IdData> findIdDataByIdExceptStudentDataForAInstitute(String instituteId);

  @Query("SELECT id, institute_id, form_data, session, user_form_request, created_at FROM id_data WHERE id = ?0 ALLOW FILTERING")
  IdData findIdDataByIdExceptStudentData(UUID id);
}
