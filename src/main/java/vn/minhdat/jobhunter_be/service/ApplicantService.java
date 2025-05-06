package vn.minhdat.jobhunter_be.service;

import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.entity.Applicant;
import vn.minhdat.jobhunter_be.repository.ApplicantRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ApplicantService {
    private final ApplicantRepository applicantRepository;

    public ApplicantService(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    public Applicant handleCreateApplicant(Applicant applicant) {
        return this.applicantRepository.save(applicant);
    }

    public void handleDeleteApplicant(long id) {
        this.applicantRepository.deleteById(id);
    }

    public Applicant handleUpdateApplicant(Applicant applicant) {
        Applicant currentApplicant = this.handleGetApplicantById(applicant.getUserId());

        if(currentApplicant != null) {
            currentApplicant.setAddress(applicant.getAddress());
            currentApplicant.setContact(applicant.getContact());
            currentApplicant.setDob(applicant.getDob());
            currentApplicant.setFullName(applicant.getFullName());
            currentApplicant.setGender(applicant.getGender());
            currentApplicant.setUsername(applicant.getUsername());
            currentApplicant.setAvailableStatus(applicant.isAvailableStatus());
            currentApplicant.setEducation(applicant.getEducation());
            currentApplicant.setLevel(applicant.getLevel());
            currentApplicant.setResumeUrl(applicant.getResumeUrl());

            return this.applicantRepository.save(currentApplicant);
        }

        return null;
    }

    public Applicant handleGetApplicantById(long id) {
        Optional<Applicant> result = this.applicantRepository.findById(id);

        if(result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public ArrayList<Applicant> handleGetAllApplicants() {
        return (ArrayList<Applicant>) this.applicantRepository.findAll();
    }
}
