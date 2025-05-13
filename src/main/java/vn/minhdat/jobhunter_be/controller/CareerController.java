package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Career;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.CareerService;
import vn.minhdat.jobhunter_be.util.annotation.ApiMessage;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class CareerController {
    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @PostMapping("/careers")
    public ResponseEntity<Career> createCareer(@Valid @RequestBody Career career) throws InvalidException {
        if(career.getName() != null && this.careerService.handleExistCareer(career.getName())) {
            throw new InvalidException("Career exists");
        }
        Career newCareer = this.careerService.handleCreateCareer(career);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCareer);
    }

    @PutMapping("/careers")
    public ResponseEntity<Career> updateCareer(@Valid @RequestBody Career career) throws InvalidException {
        if(career.getName() != null && this.careerService.handleExistCareer(career.getName())) {
            throw new InvalidException("Career exists");
        }
        Career updateCareer = this.careerService.handleUpdateCareer(career);

        if(updateCareer == null){
            throw new InvalidException("Career doesn't exist");
        }

        return ResponseEntity.status(HttpStatus.OK).body(updateCareer);
    }

    @DeleteMapping("/careers/{id}")
    @ApiMessage("Delete career by id")
    public ResponseEntity<Void> deleteCareer(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");

        if(pattern.matcher(id).matches()){
            Career career = this.careerService.handleGetCareerById(Long.parseLong(id));
            if(career != null){
                this.careerService.handleDeleteCareer(Long.parseLong(id));
                return ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                throw new InvalidException("Career doesn't exist");
            }
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/careers/{id}")
    public ResponseEntity<Career> getCareerById(@PathVariable("id") String id) throws InvalidException {
        Pattern pattern = Pattern.compile("^[0-9]+$");

        if(pattern.matcher(id).matches()){
            Career career = this.careerService.handleGetCareerById(Long.parseLong(id));
            if(career != null){
                return ResponseEntity.status(HttpStatus.OK).body(career);
            } else {
                throw new InvalidException("Career doesn't exist");
            }
        } else {
            throw new InvalidException("Id is number");
        }
    }

    @GetMapping("/careers")
    public ResponseEntity<ResultPaginationResponse> getAllCareers(
            @Filter Specification<Career> spec, Pageable pageable
    ) {
        ResultPaginationResponse result = this.careerService.handleGetAllCareers(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
