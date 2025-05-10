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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.minhdat.jobhunter_be.dto.request.LoginRequest;
import vn.minhdat.jobhunter_be.dto.response.LoginResponse;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.service.UserService;
import vn.minhdat.jobhunter_be.util.SecurityUtil;

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
            loginResponse.setUserLogin(userLogin);
        }
        String accessToken = this.securityUtil.createAccessToken(authentication);
        loginResponse.setAccessToken(accessToken);

        String refreshToken = this.securityUtil.createRefreshToken(loginRequest.getEmail(), loginResponse);
        this.userService.updateRefreshToken(loginRequest.getEmail(), refreshToken);

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
}
