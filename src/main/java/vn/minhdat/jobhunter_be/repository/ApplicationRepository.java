package vn.minhdat.jobhunter_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.minhdat.jobhunter_be.entity.Applicant;
import vn.minhdat.jobhunter_be.entity.Application;
import vn.minhdat.jobhunter_be.entity.Job;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>
        , JpaSpecificationExecutor<Application> {
    List<Application> findByApplicant(Applicant applicant);
    List<Application> findByJob(Job job);
}
