package vn.minhdat.jobhunter_be.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

        String accessToken = this.securityUtil.createToken(authentication);
        loginResponse.setAccessToken(accessToken);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
}
