package vn.minhdat.jobhunter_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.minhdat.jobhunter_be.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByContact_Email(String email);
    boolean existsByContact_Email(String email);
//    User findByRefreshTokenAndContact_Email(String refreshToken, String email);
}
