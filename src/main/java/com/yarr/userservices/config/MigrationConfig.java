package com.yarr.userservices.config;

import com.yarr.userservices.converters.StudentDataConverter;
import com.yarr.userservices.entity.IdData;
import com.yarr.userservices.entity.StudentData;
import com.yarr.userservices.entity.StudentDataEntity;
import com.yarr.userservices.repository.IdDataRepository;
import com.yarr.userservices.repository.StudentDataRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.cassandra.core.CassandraTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class MigrationConfig {

  @Autowired(required = false)
  private IdDataRepository idDataRepository;

  @Autowired(required = false)
  private StudentDataRepository studentDataRepository;

  @Autowired(required = false)
  private StudentDataConverter studentDataConverter;

  @Autowired(required = false)
  private CassandraTemplate cassandraTemplate;

  @Bean
  public StudentDataConverter studentDataConverter() {
    return new StudentDataConverter();
  }

  /**
   * This method will run once after application startup to migrate existing data
   * from the old UDT structure to the new separate table structure.
   */
  @PostConstruct
  @DependsOn("cassandraScript")
  public void migrateStudentData() {
    try {
      // Validate dependencies are available
      if (idDataRepository == null || studentDataRepository == null ||
          studentDataConverter == null || cassandraTemplate == null) {
        System.err.println("Migration skipped - required dependencies not available");
        return;
      }

      // Check if the old table structure exists
      boolean oldStructureExists = false;
      try {
        oldStructureExists = cassandraTemplate.getCqlOperations()
            .queryForObject(
                "SELECT COUNT(*) FROM system_schema.columns WHERE keyspace_name = 'idservices' AND table_name = 'id_data' AND column_name = 'student_data'",
                Long.class) > 0;
      } catch (Exception e) {
        System.err.println("Error checking old structure: " + e.getMessage());
        return;
      }

      if (oldStructureExists) {
        // Fetch all IdData records with the old structure
        List<Object[]> oldRecords = cassandraTemplate.getCqlOperations()
            .queryForList("SELECT id, student_data FROM idservices.id_data", Object[].class);

        for (Object[] record : oldRecords) {
          UUID idDataId = (UUID) record[0];
          @SuppressWarnings("unchecked")
          List<StudentData> studentDataList = (List<StudentData>) record[1];

          if (studentDataList != null && !studentDataList.isEmpty()) {
            // Convert to new entity structure
            List<StudentDataEntity> entities = studentDataConverter.toStudentDataEntityList(studentDataList, idDataId);

            // Save to new table
            studentDataRepository.saveAll(entities);

            // Update IdData to include student_ids
            IdData idData = idDataRepository.findById(idDataId).orElse(null);
            if (idData != null) {
              List<String> studentIds = new ArrayList<>();
              for (StudentData studentData : studentDataList) {
                studentIds.add(studentData.getStudent_id());
              }
              idData.setStudent_ids(studentIds);
              idDataRepository.save(idData);
            }
          }
        }

        // After migration is complete, you might want to drop the old column
        // Note: This is a destructive operation, so you may want to handle this
        // manually
        // cassandraTemplate.getCqlOperations().execute("ALTER TABLE idservices.id_data
        // DROP student_data");
      }
    } catch (Exception e) {
      // Log the error but don't fail startup
      System.err.println("Migration failed: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
