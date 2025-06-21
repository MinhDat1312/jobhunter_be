package vn.minhdat.jobhunter_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.request.LoginRequest;
import vn.minhdat.jobhunter_be.dto.response.ApplicantResponse;
import vn.minhdat.jobhunter_be.dto.response.LoginResponse;
import vn.minhdat.jobhunter_be.dto.response.RecruiterResponse;
import vn.minhdat.jobhunter_be.entity.Applicant;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.ApplicantService;
import vn.minhdat.jobhunter_be.service.RecruiterService;
import vn.minhdat.jobhunter_be.service.UserService;
import vn.minhdat.jobhunter_be.util.SecurityUtil;
import vn.minhdat.jobhunter_be.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final ApplicantService applicantService;
    private final RecruiterService recruiterService;
    private final PasswordEncoder passwordEncoder;

    @Value("${minhdat.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshToken;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                          UserService userService, PasswordEncoder passwordEncoder,
                          ApplicantService applicantService, RecruiterService recruiterService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.applicantService = applicantService;
        this.recruiterService = recruiterService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse loginResponse = new LoginResponse();
        User currentUser = this.userService.handleGetUserByEmail(loginRequest.getEmail());
        if (currentUser != null) {
            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(
                    currentUser.getUserId(), currentUser.getContact().getEmail(),
                    currentUser.getFullName(), currentUser.getUsername(), currentUser.getRole()
            );
            loginResponse.setUser(userLogin);
        }
        String accessToken = this.securityUtil.createAccessToken(loginRequest.getEmail(), loginResponse);
        loginResponse.setAccessToken(accessToken);

        String refreshToken = this.securityUtil.createRefreshToken(loginRequest.getEmail(), loginResponse);
        this.userService.handleUpdateRefreshToken(loginRequest.getEmail(), refreshToken);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtRefreshToken)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/auth/register/applicant")
    public ResponseEntity<ApplicantResponse> registerApplicant(@Valid @RequestBody Applicant applicant)
            throws InvalidException {
        if(this.userService.handleExistsByEmail(applicant.getContact().getEmail())) {
            throw new InvalidException("Email exists: " + applicant.getContact().getEmail());
        }

        String hashPassword = passwordEncoder.encode(applicant.getPassword());
        applicant.setPassword(hashPassword);

        Applicant newApplicant = this.applicantService.handleCreateApplicant(applicant);
        ApplicantResponse applicantResponse = this.applicantService.convertToApplicantResponse(newApplicant);
        return ResponseEntity.status(HttpStatus.CREATED).body(applicantResponse);
    }

    @PostMapping("/auth/register/recruiter")
    public ResponseEntity<RecruiterResponse> registerRecruiter(@Valid @RequestBody Recruiter recruiter)
            throws InvalidException {
        if(this.userService.handleExistsByEmail(recruiter.getContact().getEmail())) {
            throw new InvalidException("Email exists: " + recruiter.getContact().getEmail());
        }

        String hashPassword = passwordEncoder.encode(recruiter.getPassword());
        recruiter.setPassword(hashPassword);

        Recruiter newRecruiter = this.recruiterService.handleCreateRecruiter(recruiter);
        RecruiterResponse recruiterResponse = this.recruiterService.convertToRecruiterResponse(newRecruiter);
        return ResponseEntity.status(HttpStatus.CREATED).body(recruiterResponse);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get information account")
    public ResponseEntity<LoginResponse.UserGetAccount> getCurrentAccount(){
        String currentEmail = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUser = this.userService.handleGetUserByEmail(currentEmail);
        LoginResponse.UserLogin currentUserLogin = new LoginResponse.UserLogin();
        LoginResponse.UserGetAccount userGetAccount = new LoginResponse.UserGetAccount();
        if(currentUser != null) {
            currentUserLogin.setUserId(currentUser.getUserId());
            currentUserLogin.setFullName(currentUser.getFullName());
            currentUserLogin.setUsername(currentUser.getUsername());
            currentUserLogin.setEmail(currentUser.getContact().getEmail());
            currentUserLogin.setRole(currentUser.getRole());

            userGetAccount.setUser(currentUserLogin);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Refresh account")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", defaultValue = "missingValue")
            String refreshToken
    ) throws InvalidException {
        if(refreshToken.equalsIgnoreCase("missingValue")) {
            throw new InvalidException("You don't have a refresh token at cookie");
        }

        Jwt jwt = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = jwt.getSubject();

        User currentUser = this.userService.handleGetUserByRefreshTokenAndEmail(refreshToken, email);
        if(currentUser == null){
            throw new InvalidException("User not found");
        }

        LoginResponse loginResponse = new LoginResponse();

        LoginResponse.UserLogin currentUserLogin = new LoginResponse.UserLogin();
        currentUserLogin.setUserId(currentUser.getUserId());
        currentUserLogin.setFullName(currentUser.getFullName());
        currentUserLogin.setEmail(currentUser.getContact().getEmail());
        currentUserLogin.setRole(currentUser.getRole());

        loginResponse.setUser(currentUserLogin);

        String newAccessToken = this.securityUtil.createAccessToken(email, loginResponse);
        loginResponse.setAccessToken(newAccessToken);

        String newRefreshToken = this.securityUtil.createRefreshToken(email, loginResponse);
        this.userService.handleUpdateRefreshToken(email, newRefreshToken);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtRefreshToken)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout account")
    public ResponseEntity<Void> logout() throws InvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if(email.isEmpty()){
            throw new InvalidException("Access token is invalid");
        }

        this.userService.handleUpdateRefreshToken(email, null);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(null);
    }
}
