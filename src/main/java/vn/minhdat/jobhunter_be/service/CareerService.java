package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Career;
import vn.minhdat.jobhunter_be.repository.CareerRepository;
import vn.minhdat.jobhunter_be.repository.JobRepository;

import java.util.Optional;

@Service
public class CareerService {
    private final CareerRepository careerRepository;
    private final JobRepository jobRepository;

    public CareerService(CareerRepository careerRepository, JobRepository jobRepository) {
        this.careerRepository = careerRepository;
        this.jobRepository = jobRepository;
    }

    public Career handleCreateCareer(Career career) {
        return this.careerRepository.save(career);
    }

    public Career handleUpdateCareer(Career career) {
        Career currentCareer = this.handleGetCareerById(career.getCareerId());

        if(currentCareer != null){
            currentCareer.setName(career.getName());
            currentCareer.setDescription(career.getDescription());
            return this.careerRepository.save(currentCareer);
        }
        return null;
    }

    public void handleDeleteCareer(long id) {
        Career currentCareer = this.handleGetCareerById(id);

        if(currentCareer.getJobs() != null){
            currentCareer.getJobs().forEach(job -> {
                job.setCareer(null);
                jobRepository.save(job);
            });
        }

        this.careerRepository.deleteById(currentCareer.getCareerId());
    }

    public Career handleGetCareerById(long id) {
        Optional<Career> career = this.careerRepository.findById(id);

        if(career.isPresent()) {
            return career.get();
        }
        return null;
    }

    public ResultPaginationResponse handleGetAllCareers(Specification<Career> spec, Pageable pageable) {
        Page<Career> page = this.careerRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setCurrentPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        return new ResultPaginationResponse(meta, page.getContent());
    }

    public boolean handleExistCareer(String name) {
        return this.careerRepository.existsByName(name);
    }
}
