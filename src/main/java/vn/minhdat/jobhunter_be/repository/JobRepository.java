package vn.minhdat.jobhunter_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.minhdat.jobhunter_be.entity.Career;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.entity.Skill;

import java.util.Collection;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findByRecruiter(Recruiter recruiter);
    List<Job> findByCareer(Career career);
    List<Job> findBySkillsIn(List<Skill> skills);
    long countByActive(boolean active);
}
