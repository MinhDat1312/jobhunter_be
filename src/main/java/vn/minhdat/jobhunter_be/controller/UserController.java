package vn.minhdat.jobhunter_be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.request.SaveJobRequest;
import vn.minhdat.jobhunter_be.dto.request.UpdatePasswordRequest;
import vn.minhdat.jobhunter_be.dto.response.LoginResponse;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.dto.response.UserResponse;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.UserService;
import vn.minhdat.jobhunter_be.util.SecurityUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @Value("${minhdat.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshToken;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationResponse> getAllUsers(
            @Filter Specification<User> spec, Pageable pageable
    ) {
        ResultPaginationResponse result = this.userService.handleGetAllUsers(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/users")
    public <T extends User> ResponseEntity<T> getUserByEmail() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User user =  this.userService.handleGetUserByEmail(email);
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK).body((T) user);
    }

    @PutMapping("/users/update-password")
    public ResponseEntity<LoginResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest)
            throws InvalidException {
        boolean checked = this.userService.handleCheckCurrentPassword(updatePasswordRequest.getCurrentPassword());
        if(!checked) {
            throw new InvalidException("Current password is error");
        }

        Map<String, Object> result = this.userService.handleUpdatePassword(updatePasswordRequest.getNewPassword());
        if(result == null) {
            throw new InvalidException("Updated password is failed");
        }
        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", result.get("refreshToken").toString())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtRefreshToken)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body((LoginResponse) result.get("loginResponse"));
    }

    @PutMapping("/users/saved-jobs")
    public ResponseEntity<UserResponse> saveJobs(@Valid @RequestBody SaveJobRequest saveJobRequest)
            throws InvalidException
    {
        User user = this.userService.handleGetUserById(saveJobRequest.getUserId());
        if(user == null) {
            throw new InvalidException("User not found");
        }

        UserResponse userResponse = this.userService.handleSaveJobs(saveJobRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

}
