package com.yarr.userservices.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yarr.userservices.entity.Institute;
import com.yarr.userservices.repository.InstituteRepository;

@Service
public class InstituteService {
  private final InstituteRepository instituteRepository;

  public InstituteService(InstituteRepository instituteRepository) {
    this.instituteRepository = instituteRepository;
  }

  @Transactional(readOnly = true)
  public Institute getInstituteByCode(String instituteCode) {
    return instituteRepository.findByInstituteCode(instituteCode);
  }

  @Transactional(readOnly = true)
  public Institute getInstituteByEmail(String instituteEmail) {
    return instituteRepository.findByInstituteEmail(instituteEmail);
  }

  public List<Institute> getAllInstitutes() {
    return instituteRepository.findAll();
  }

  public Institute registerNewInstitute(Institute institute) {
    Institute existingInstitute = instituteRepository.findByInstituteCode(institute.getInstituteCode());
    if (existingInstitute != null) {
      return null;
    }

    return instituteRepository.save(institute);
  }

  public Institute approveInstituteRegistration(UUID instituteId) {
    Institute institute = instituteRepository.findById(instituteId).orElse(null);
    if (institute == null) {
      return null;
    }

    institute.setInstituteApproval("APPROVED");
    // Generate a unique institute code
    String instituteName = institute.getInstituteName();
    String[] nameParts = instituteName.split(" ");
    StringBuilder codeBuilder = new StringBuilder("INS");

    for (String part : nameParts) {
      if (part.length() > 3) {
        codeBuilder.append(part.substring(0, 1).toUpperCase());
      }
    }

    String zipCode = institute.getInstituteZip();
    String city = institute.getInstituteCity();

    codeBuilder.append(zipCode.substring(zipCode.length() - 4).toUpperCase());
    codeBuilder.append(city.substring(0, 3).toUpperCase());

    String instituteCode = codeBuilder.toString();

    institute.setInstituteCode(instituteCode);
    instituteRepository.save(institute);

    return institute;
  }

  public String rejectInstituteRegistration(UUID instituteId) {
    Institute institute = instituteRepository.findById(instituteId).orElse(null);
    if (institute == null) {
      return "Institute not found";
    }

    institute.setInstituteApproval("REJECTED");
    instituteRepository.save(institute);

    return "Institute rejected successfully";
  }
}
