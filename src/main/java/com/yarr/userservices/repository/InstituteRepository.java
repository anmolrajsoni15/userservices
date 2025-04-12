package com.yarr.userservices.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yarr.userservices.entity.Institute;

public interface InstituteRepository extends JpaRepository<Institute, UUID> {
    Institute findByInstituteCode(String instituteCode);
    Institute findByInstituteEmail(String instituteEmail);  
}
