package vn.minhdat.jobhunter_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.minhdat.jobhunter_be.entity.Recruiter;

import java.util.List;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long>,
        JpaSpecificationExecutor<Recruiter> {
    List<Recruiter> findByUserIdIn(List<Long> userIds);
}
