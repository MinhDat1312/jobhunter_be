package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.response.JobResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.JobService;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody Job job) {
        JobResponse newJob = this.jobService.handleCreateJob(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(newJob);
    }

    @PutMapping("/jobs")
    public ResponseEntity<JobResponse> updateJob(@Valid @RequestBody Job job) throws InvalidException {
        Job currentJob = this.jobService.handleGetJobById(job.getJobId());
        if(currentJob != null){
            JobResponse updateJob = this.jobService.handleUpdateJob(job);
            return ResponseEntity.status(HttpStatus.OK).body(updateJob);
        } else {
            throw new InvalidException("Job doesn't exist");
        }
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if (pattern.matcher(id).matches()) {
            if (this.jobService.handleGetJobById(Long.parseLong(id)) != null) {
                this.jobService.handleDeleteJob(Long.parseLong(id));
                return ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                throw new InvalidException("Job doesn't exist");
            }
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        if (pattern.matcher(id).matches()) {
            Job currentJob = this.jobService.handleGetJobById(Long.parseLong(id));
            if (currentJob == null) {
                throw new InvalidException("Job doesn't exist");
            }
            return ResponseEntity.status(HttpStatus.OK).body(currentJob);
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationResponse> getAllJobs(
            @Filter Specification<Job> spec, Pageable pageable
    ) {
        ResultPaginationResponse result = this.jobService.handleGetAllJobs(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
