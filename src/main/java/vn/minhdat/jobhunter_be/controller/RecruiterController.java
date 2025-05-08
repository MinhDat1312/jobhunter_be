package vn.minhdat.jobhunter_be.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.RecruiterService;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class RecruiterController {
    private final RecruiterService recruiterService;
    private final PasswordEncoder passwordEncoder;

    public RecruiterController(RecruiterService recruiterService, PasswordEncoder passwordEncoder) {
        this.recruiterService = recruiterService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/recruiters")
    public ResponseEntity<Recruiter> createRecruiter(@Valid @RequestBody Recruiter recruiter) {
        String hashPassword = passwordEncoder.encode(recruiter.getPassword());
        recruiter.setPassword(hashPassword);

        Recruiter newRecruiter = this.recruiterService.handleCreateRecruiter(recruiter);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRecruiter);
    }

    @DeleteMapping("/recruiters/{id}")
    public ResponseEntity<String> deleteRecruiter(@PathVariable("id") long id) {
        this.recruiterService.handleDeleteRecruiter(id);
        return ResponseEntity.status(HttpStatus.OK).body("Recruiter deleted successfully");
    }

    @PutMapping("/recruiters")
    public ResponseEntity<Recruiter> updateRecruiter(@Valid @RequestBody Recruiter recruiter) throws InvalidException {
        Recruiter newRecruiter = this.recruiterService.handleUpdateRecruiter(recruiter);
        if(newRecruiter != null) {
            return ResponseEntity.status(HttpStatus.OK).body(newRecruiter);
        } else {
            throw new InvalidException("Recruiter not found");
        }
    }

    @GetMapping("/recruiters/{id}")
    public ResponseEntity<Recruiter> getRecruiterById(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");

        if(pattern.matcher(id).matches()){
            Recruiter recruiter = this.recruiterService.handleGetRecruiterById(Long.parseLong(id));
            if(recruiter != null){
                return ResponseEntity.status(HttpStatus.OK).body(recruiter);
            } else {
                throw new InvalidException("Recruiter not found");
            }
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/recruiters")
    public ResponseEntity<ResultPaginationResponse> getAllRecruiters(
            @RequestParam("currentPage") Optional<String> currentPage,
            @RequestParam("pageSize") Optional<String> pageSize
    ) throws InvalidException {
        String currentPageString = currentPage.isPresent() ? currentPage.get() : "";
        String pageSizeString = pageSize.isPresent() ? pageSize.get() : "";

        Pattern pattern = Pattern.compile("^[0-9]+$");
        if(pattern.matcher(currentPageString).matches() && pattern.matcher(pageSizeString).matches()){
            Pageable pageable = PageRequest.of(Integer.parseInt(currentPageString) - 1,
                    Integer.parseInt(pageSizeString));

            ResultPaginationResponse result = this.recruiterService.handleGetAllRecruiters(pageable);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            throw new InvalidException("currentPage and pageSize is number or not empty");
        }
    }
}
