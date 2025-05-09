package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.response.RecruiterResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.RecruiterService;
import vn.minhdat.jobhunter_be.service.UserService;
import vn.minhdat.jobhunter_be.util.annotation.ApiMessage;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class RecruiterController {
    private final RecruiterService recruiterService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RecruiterController(RecruiterService recruiterService, UserService userService,
                               PasswordEncoder passwordEncoder) {
        this.recruiterService = recruiterService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/recruiters")
    public ResponseEntity<RecruiterResponse> createRecruiter(@Valid @RequestBody Recruiter recruiter)
            throws InvalidException {
        if(userService.handleExistsByEmail(recruiter.getContact().getEmail())) {
            throw new InvalidException("Email exists: " + recruiter.getContact().getEmail());
        }

        String hashPassword = passwordEncoder.encode(recruiter.getPassword());
        recruiter.setPassword(hashPassword);

        Recruiter newRecruiter = this.recruiterService.handleCreateRecruiter(recruiter);
        RecruiterResponse recruiterResponse = this.recruiterService.convertToRecruiterResponse(newRecruiter);
        return ResponseEntity.status(HttpStatus.CREATED).body(recruiterResponse);
    }

    @DeleteMapping("/recruiters/{id}")
    @ApiMessage("Delete a recruiter")
    public ResponseEntity<Void> deleteRecruiter(@PathVariable("id") long id) {
        this.recruiterService.handleDeleteRecruiter(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/recruiters")
    public ResponseEntity<RecruiterResponse> updateRecruiter(@Valid @RequestBody Recruiter recruiter)
            throws InvalidException {
        Recruiter newRecruiter = this.recruiterService.handleUpdateRecruiter(recruiter);
        if(newRecruiter != null) {
            RecruiterResponse recruiterResponse = this.recruiterService.convertToRecruiterResponse(newRecruiter);
            return ResponseEntity.status(HttpStatus.OK).body(recruiterResponse);
        } else {
            throw new InvalidException("Recruiter not found");
        }
    }

    @GetMapping("/recruiters/{id}")
    public ResponseEntity<RecruiterResponse> getRecruiterById(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");

        if(pattern.matcher(id).matches()){
            Recruiter recruiter = this.recruiterService.handleGetRecruiterById(Long.parseLong(id));
            if(recruiter != null){
                RecruiterResponse recruiterResponse = this.recruiterService.convertToRecruiterResponse(recruiter);
                return ResponseEntity.status(HttpStatus.OK).body(recruiterResponse);
            } else {
                throw new InvalidException("Recruiter not found");
            }
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/recruiters")
    public ResponseEntity<ResultPaginationResponse> getAllRecruiters(
            @Filter Specification<Recruiter> spec, Pageable pageable
    ) {
        ResultPaginationResponse result = this.recruiterService.handleGetAllRecruiters(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
