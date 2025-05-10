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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.minhdat.jobhunter_be.dto.request.LoginRequest;
import vn.minhdat.jobhunter_be.dto.response.LoginResponse;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.service.UserService;
import vn.minhdat.jobhunter_be.util.SecurityUtil;
import vn.minhdat.jobhunter_be.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${minhdat.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshToken;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
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
                    currentUser.getUserId(), currentUser.getContact().getEmail(), currentUser.getFullName()
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
            currentUserLogin.setEmail(currentUser.getContact().getEmail());

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
