package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ApplicationResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Applicant;
import vn.minhdat.jobhunter_be.entity.Application;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.repository.ApplicantRepository;
import vn.minhdat.jobhunter_be.repository.ApplicationRepository;
import vn.minhdat.jobhunter_be.repository.JobRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ApplicantRepository applicantRepository;

    public ApplicationService(ApplicationRepository applicationRepository, ApplicantRepository applicantRepository,
                              JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.applicantRepository = applicantRepository;
    }

    public ApplicationResponse handleCreateApplication(Application application) {
        Application result = this.applicationRepository.save(application);

        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setId(result.getApplicationId());
        applicationResponse.setCreatedAt(result.getCreatedAt());
        applicationResponse.setCreatedBy(result.getCreatedBy());

        return applicationResponse;
    }

    public ApplicationResponse handleUpdateApplication(Application application) {
        Application resApplication = this.handleGetApplicationById(application.getApplicationId());
        resApplication.setStatus(application.getStatus());
        Application result = this.applicationRepository.save(resApplication);

        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setId(result.getApplicationId());
        applicationResponse.setUpdatedAt(result.getUpdatedAt());
        applicationResponse.setUpdatedBy(result.getUpdatedBy());

        return applicationResponse;
    }

    public void handleDeleteApplication(long id) {
        this.applicationRepository.deleteById(id);
    }

    public Application handleGetApplicationById(long id) {
        Optional<Application> application = this.applicationRepository.findById(id);
        return application.orElse(null);
    }

    public ResultPaginationResponse handleGetAllApplications(
            Specification<Application> spec, Pageable pageable
    ) {
        Page<Application> page = this.applicationRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<ApplicationResponse> applications = page.getContent().stream()
                .map(this :: convertToApplicationResponse)
                .toList();

        return new ResultPaginationResponse(meta, applications);
    }

    public boolean checkApplicantAndJobExist(Application application) {
        if(application.getApplicant() == null || application.getJob() == null) {
            return false;
        }

        Optional<Applicant> applicant = this.applicantRepository.findById(application.getApplicant().getUserId());
        Optional<Job> job = this.jobRepository.findById(application.getJob().getJobId());

        return applicant.isPresent() && job.isPresent();
    }

    public Applicant handleGetApplicant(Application application) {
        Optional<Applicant> applicant = this.applicantRepository.findById(application.getApplicant().getUserId());
        return applicant.orElse(null);
    }

    public Job handleGetJob(Application application) {
        Optional<Job> job = this.jobRepository.findById(application.getJob().getJobId());
        return job.orElse(null);
    }

    public ApplicationResponse convertToApplicationResponse(Application application) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        Applicant applicant = this.handleGetApplicant(application);
        Job job = this.handleGetJob(application);

        applicationResponse.setId(application.getApplicationId());
        applicationResponse.setEmail(application.getEmail());
        applicationResponse.setUrl(application.getResumeUrl());
        applicationResponse.setRecruiterName(job.getRecruiter().getFullName());
        applicationResponse.setStatus(application.getStatus());
        applicationResponse.setCreatedAt(application.getCreatedAt());
        applicationResponse.setCreatedBy(application.getCreatedBy());
        applicationResponse.setUpdatedAt(application.getUpdatedAt());
        applicationResponse.setUpdatedBy(application.getUpdatedBy());

        ApplicationResponse.UserApplication user = new ApplicationResponse.UserApplication(
                applicant.getUserId(), applicant.getFullName()
        );
        applicationResponse.setUser(user);

        ApplicationResponse.JobApplication resJob = new ApplicationResponse.JobApplication(
                job.getJobId(), job.getTitle()
        );
        applicationResponse.setJob(resJob);

        return applicationResponse;
    }
}
