package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.repository.RecruiterRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;

    public RecruiterService(RecruiterRepository recruiterRepository) {
        this.recruiterRepository = recruiterRepository;
    }

    public Recruiter handleCreateRecruiter(Recruiter recruiter) {
        return this.recruiterRepository.save(recruiter);
    }

    public void handleDeleteRecruiter(long id) {
        this.recruiterRepository.deleteById(id);
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
            currentRecruiter.setLogo(updateRecruiter.getLogo());
            currentRecruiter.setWebsite(updateRecruiter.getWebsite());

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

    public ResultPaginationResponse handleGetAllRecruiters(Pageable pageable) {
        Page<Recruiter> page = this.recruiterRepository.findAll(pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setCurrentPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        return new ResultPaginationResponse(meta, page.getContent());
    }
}
