package vn.minhdat.jobhunter_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.minhdat.jobhunter_be.entity.Career;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long>, JpaSpecificationExecutor<Career> {
    boolean existsByName(String name);
}
