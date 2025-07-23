package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.RecruiterResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.dto.response.UserResponse;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.entity.Role;
import vn.minhdat.jobhunter_be.repository.BlogRepository;
import vn.minhdat.jobhunter_be.repository.CommentRepository;
import vn.minhdat.jobhunter_be.repository.JobRepository;
import vn.minhdat.jobhunter_be.repository.RecruiterRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final RoleService roleService;
    private final String HR = "HR";

    public RecruiterService(RecruiterRepository recruiterRepository, JobRepository jobRepository,
                            CommentRepository commentRepository, RoleService roleService,
                            BlogRepository blogRepository
    ) {
        this.recruiterRepository = recruiterRepository;
        this.jobRepository = jobRepository;
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.roleService = roleService;
    }

    public Recruiter handleCreateRecruiter(Recruiter recruiter) {
        Role role = null;
        if(recruiter.getRole() != null) {
            role = this.roleService.handleGetRoleById(recruiter.getRole().getRoleId());
        } else {
            role = this.roleService.handleGetRoleByName(HR);
        }
        recruiter.setRole(role);
        return this.recruiterRepository.save(recruiter);
    }

    public void handleDeleteRecruiter(long id) {
        Recruiter recruiter = this.handleGetRecruiterById(id);

        if(recruiter != null){
            if(recruiter.getJobs() != null){
                List<Job> jobs = this.jobRepository.findByRecruiter(recruiter);
                this.jobRepository.deleteAll(jobs);
            }
            if(recruiter.getUsers() != null){
                recruiter.getUsers().forEach(user -> {
                    user.getFollowedRecruiters().remove(recruiter);
                });
            }
            if(recruiter.getBlogs() != null){
                this.blogRepository.deleteAll(recruiter.getBlogs());
            }
            if(recruiter.getComments() != null){
                this.commentRepository.deleteAll(recruiter.getComments());
            }
            this.recruiterRepository.deleteById(id);
        }
    }

    public Recruiter handleUpdateRecruiter(Recruiter updateRecruiter) {
        Recruiter currentRecruiter = this.handleGetRecruiterById(updateRecruiter.getUserId());

        if(currentRecruiter != null) {
            currentRecruiter.setAddress(updateRecruiter.getAddress());
            currentRecruiter.setContact(updateRecruiter.getContact());
            currentRecruiter.setDob(updateRecruiter.getDob());
            currentRecruiter.setFullName(updateRecruiter.getFullName());
            currentRecruiter.setGender(updateRecruiter.getGender());
            currentRecruiter.setUsername(updateRecruiter.getUsername());
            currentRecruiter.setDescription(updateRecruiter.getDescription());
            currentRecruiter.setAvatar(updateRecruiter.getAvatar());
            currentRecruiter.setWebsite(updateRecruiter.getWebsite());

            if(updateRecruiter.getRole() != null) {
                Role role = this.roleService.handleGetRoleById(updateRecruiter.getRole().getRoleId());
                currentRecruiter.setRole(role);
            }

            return this.recruiterRepository.save(currentRecruiter);
        }

        return null;
    }

    public Recruiter handleGetRecruiterById(long id) {
        Optional<Recruiter> recruiter = this.recruiterRepository.findById(id);

        if(recruiter.isPresent()) {
            return recruiter.get();
        }

        return null;
    }

    public ResultPaginationResponse handleGetAllRecruiters(Specification<Recruiter> spec, Pageable pageable) {
        Page<Recruiter> page = this.recruiterRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<RecruiterResponse> recruiterResponses = page.getContent().stream()
                                                            .map(this :: convertToRecruiterResponse)
                                                            .toList();

        return new ResultPaginationResponse(meta, recruiterResponses);
    }

    public RecruiterResponse convertToRecruiterResponse(Recruiter recruiter) {
        RecruiterResponse recruiterResponse = new RecruiterResponse();

        recruiterResponse.setUserId(recruiter.getUserId());
        recruiterResponse.setContact(recruiter.getContact());
        recruiterResponse.setAddress(recruiter.getAddress());
        recruiterResponse.setUsername(recruiter.getUsername());
        recruiterResponse.setFullName(recruiter.getFullName());
        recruiterResponse.setCreatedAt(recruiter.getCreatedAt());
        recruiterResponse.setUpdatedAt(recruiter.getUpdatedAt());
        recruiterResponse.setDescription(recruiter.getDescription());
        recruiterResponse.setWebsite(recruiter.getWebsite());
        recruiterResponse.setAvatar(recruiter.getAvatar());

        if(recruiter.getRole() != null) {
            UserResponse.RoleUser roleUser = new UserResponse.RoleUser();
            roleUser.setRoleId(recruiter.getRole().getRoleId());
            roleUser.setName(recruiter.getRole().getName());

            recruiterResponse.setRole(roleUser);
        }

        return recruiterResponse;
    }
}
