package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.response.ApplicationResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Applicant;
import vn.minhdat.jobhunter_be.entity.Application;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.ApplicationService;
import vn.minhdat.jobhunter_be.util.annotation.ApiMessage;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody Application application)
            throws InvalidException {
        boolean checkUserAndJob = this.applicationService.checkApplicantAndJobExist(application);
        if(!checkUserAndJob){
            throw new InvalidException("User or Job doesn't exist");
        }

        Applicant applicant = this.applicationService.handleGetApplicant(application);
        if(applicant.getResumeUrl() == null){
            throw new InvalidException("Resume is required");
        }

        application.setResumeUrl(applicant.getResumeUrl());
        ApplicationResponse applicationResponse = this.applicationService.handleCreateApplication(application);

        return ResponseEntity.status(HttpStatus.CREATED).body(applicationResponse);
    }

    @PutMapping("/applications")
    public ResponseEntity<ApplicationResponse> updateApplication(@Valid @RequestBody Application application)
            throws InvalidException {
        Application resApplication = this.applicationService.handleGetApplicationById(application.getApplicationId());
        if(resApplication == null){
            throw new InvalidException("Application doesn't exist");
        }

        ApplicationResponse applicationResponse = this.applicationService.handleUpdateApplication(application);
        return ResponseEntity.status(HttpStatus.OK).body(applicationResponse);
    }

    @DeleteMapping("/applications/{id}")
    @ApiMessage("Delete a application")
    public ResponseEntity<Void> deleteApplication(@PathVariable("id") long id) {
        this.applicationService.handleDeleteApplication(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable("id") String id)
            throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if (pattern.matcher(id).matches()) {
            Application currentApplication = this.applicationService.handleGetApplicationById(Long.parseLong(id));
            if (currentApplication != null) {
                ApplicationResponse applicationResponse = this.applicationService
                        .convertToApplicationResponse(currentApplication);
                return ResponseEntity.status(HttpStatus.OK).body(applicationResponse);
            } else {
                throw new InvalidException("Application doesn't exist");
            }
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/applications")
    public ResponseEntity<ResultPaginationResponse> getAllApplications(
            @Filter Specification<Application> spec, Pageable pageable) {
        ResultPaginationResponse result = this.applicationService.handleGetAllApplications(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
