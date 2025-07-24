package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ApplicantResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.dto.response.UserResponse;
import vn.minhdat.jobhunter_be.entity.Applicant;
import vn.minhdat.jobhunter_be.entity.Application;
import vn.minhdat.jobhunter_be.entity.Notification;
import vn.minhdat.jobhunter_be.entity.Role;
import vn.minhdat.jobhunter_be.repository.ApplicantRepository;
import vn.minhdat.jobhunter_be.repository.ApplicationRepository;
import vn.minhdat.jobhunter_be.repository.CommentRepository;
import vn.minhdat.jobhunter_be.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final RoleService roleService;
    private final String APPLICANT = "APPLICANT";

    public ApplicantService(ApplicantRepository applicantRepository, ApplicationRepository applicationRepository,
                            CommentRepository commentRepository, NotificationRepository notificationRepository,
                            RoleService roleService
    ) {
        this.applicantRepository = applicantRepository;
        this.applicationRepository = applicationRepository;
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
        this.roleService = roleService;
    }

    public Applicant handleCreateApplicant(Applicant applicant) {
        Role role = null;
        if(applicant.getRole() != null){
            role = this.roleService.handleGetRoleById(applicant.getRole().getRoleId());
        } else {
            role = this.roleService.handleGetRoleByName(APPLICANT);
        }
        applicant.setRole(role);
        return this.applicantRepository.save(applicant);
    }

    public void handleDeleteApplicant(long id) {
        Applicant applicant = this.handleGetApplicantById(id);
        if(applicant.getApplications() != null){
            List<Application> applications = this.applicationRepository.findByApplicant(applicant);
            this.applicationRepository.deleteAll(applications);
        }
        if(applicant.getComments() != null){
            this.commentRepository.deleteAll(applicant.getComments());
        }
        if(applicant.getActorNotifications() != null){
            List<Notification> notifications = applicant.getActorNotifications();
            this.notificationRepository.deleteAll(notifications);
        }
        if(applicant.getRecipientNotifications() != null){
            List<Notification> notifications = applicant.getRecipientNotifications();
            this.notificationRepository.deleteAll(notifications);
        }

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
            currentApplicant.setAvatar(applicant.getAvatar());

            if(applicant.getRole() != null){
                Role role = this.roleService.handleGetRoleById(applicant.getRole().getRoleId());
                currentApplicant.setRole(role);
            }

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

    public ResultPaginationResponse handleGetAllApplicants(Specification<Applicant> spec, Pageable pageable) {
        Page<Applicant> page = this.applicantRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<ApplicantResponse> applicantResponses = page.getContent().stream()
                .map(this :: convertToApplicantResponse)
                .toList();

        return new ResultPaginationResponse(meta, applicantResponses);
    }

    public ApplicantResponse convertToApplicantResponse(Applicant applicant) {
        ApplicantResponse applicantResponse = new ApplicantResponse();

        applicantResponse.setUserId(applicant.getUserId());
        applicantResponse.setContact(applicant.getContact());
        applicantResponse.setAddress(applicant.getAddress());
        applicantResponse.setUsername(applicant.getUsername());
        applicantResponse.setFullName(applicant.getFullName());
        applicantResponse.setCreatedAt(applicant.getCreatedAt());
        applicantResponse.setUpdatedAt(applicant.getUpdatedAt());
        applicantResponse.setEducation(applicant.getEducation());
        applicantResponse.setLevel(applicant.getLevel());
        applicantResponse.setGender(applicant.getGender());
        applicantResponse.setDob(applicant.getDob());
        applicantResponse.setAvailableStatus(applicant.isAvailableStatus());
        applicantResponse.setAvatar(applicant.getAvatar());

        if(applicant.getRole() != null) {
            UserResponse.RoleUser roleUser = new UserResponse.RoleUser();
            roleUser.setRoleId(applicant.getRole().getRoleId());
            roleUser.setName(applicant.getRole().getName());

            applicantResponse.setRole(roleUser);
        }

        return applicantResponse;
    }
}
