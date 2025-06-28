package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.common.Role;
import vn.minhdat.jobhunter_be.dto.response.LoginResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.dto.response.UserResponse;
import vn.minhdat.jobhunter_be.entity.Applicant;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.repository.UserRepository;
import vn.minhdat.jobhunter_be.util.SecurityUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByContact_Email(email);
    }

    public boolean handleExistsByEmail(String email) {
        return this.userRepository.existsByContact_Email(email);
    }

    public void handleUpdateRefreshToken(String email, String refreshToken) {
        User user = this.userRepository.findByContact_Email(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    public User handleGetUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndContact_Email(refreshToken, email);
    }

    public ResultPaginationResponse handleGetAllUsers (Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<UserResponse> userResponses = page.getContent().stream()
                                                            .map(this::convertToUserResponse)
                                                            .collect(Collectors.toList());

        return new ResultPaginationResponse(meta, userResponses);
    }

    public boolean handleCheckCurrentPassword(String currentPassword) {
        String currentEmail = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";

        if(!currentEmail.isEmpty()) {
            User currentUser = this.handleGetUserByEmail(currentEmail);
            return passwordEncoder.matches(currentPassword, currentUser.getPassword());
        }

        return false;
    }

    public Map<String, Object> handleUpdatePassword(String newPassword){
        String currentEmail = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";

        if(!currentEmail.isEmpty()) {
            User currentUser = this.handleGetUserByEmail(currentEmail);
            String hashedPassword = this.passwordEncoder.encode(newPassword);
            currentUser.setPassword(hashedPassword);
            User res = this.userRepository.save(currentUser);

            LoginResponse loginResponse = new LoginResponse();
            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(
                    res.getUserId(), res.getContact().getEmail(),
                    res.getFullName(), res.getUsername(), res.getAvatar(),
                    res instanceof Applicant ? Role.APPLICANT.getValue() : Role.RECRUITER.getValue(),
                    res.getRole()
            );
            loginResponse.setUser(userLogin);
            String accessToken = this.securityUtil.createAccessToken(currentEmail, loginResponse);
            loginResponse.setAccessToken(accessToken);
            String refreshToken = this.securityUtil.createRefreshToken(currentEmail, loginResponse);
            this.handleUpdateRefreshToken(currentEmail, refreshToken);

            Map<String, Object> response = new HashMap<>();
            response.put("loginResponse", loginResponse);
            response.put("refreshToken", refreshToken);

            return response;
        }

        return null;
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();

        userResponse.setUserId(user.getUserId());
        userResponse.setContact(user.getContact());
        userResponse.setAddress(user.getAddress());
        userResponse.setUsername(user.getUsername());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        if(user.getRole() != null) {
            UserResponse.RoleUser roleUser = new UserResponse.RoleUser();
            roleUser.setRoleId(user.getRole().getRoleId());
            roleUser.setName(user.getRole().getName());

            userResponse.setRole(roleUser);
        }

        return userResponse;
    }
}
