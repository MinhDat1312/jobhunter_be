package vn.minhdat.jobhunter_be.service;

import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.common.Status;
import vn.minhdat.jobhunter_be.repository.ApplicantRepository;
import vn.minhdat.jobhunter_be.repository.ApplicationRepository;
import vn.minhdat.jobhunter_be.repository.JobRepository;
import vn.minhdat.jobhunter_be.repository.RecruiterRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final ApplicantRepository applicantRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public DashboardService(ApplicantRepository applicantRepository, RecruiterRepository recruiterRepository,
                            JobRepository jobRepository, ApplicationRepository applicationRepository)
    {
        this.applicantRepository = applicantRepository;
        this.recruiterRepository = recruiterRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public Map<String, Long> statisticsUser(){
        long countApplicants = this.applicantRepository.count();
        long countRecruiters = this.recruiterRepository.count() - 1;

        return Map.of(
                "applicants", countApplicants,
                "recruiters", countRecruiters
        );
    }

    public Map<String, Long> statisticsJob(){
        long countJobActive = this.jobRepository.countByActive(true);
        long countJobInactive = this.jobRepository.countByActive(false);

        return Map.of(
                "active", countJobActive,
                "inactive", countJobInactive
        );
    }

    public Map<Status, Long> statisticsApplication(){
        List<Object[]> results = this.applicationRepository.countByStatus();

        Map<Status, Long> mapStatus = new EnumMap<>(Status.class);
        for (Object[] row : results) {
            Status status = (Status) row[0];
            Long count = (Long) row[1];
            mapStatus.put(status, count);
        }
        for (Status s : Status.values()) {
            mapStatus.putIfAbsent(s, 0L);
        }

        return mapStatus;
    }
}
