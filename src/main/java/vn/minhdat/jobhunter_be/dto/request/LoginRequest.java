package vn.minhdat.jobhunter_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "username is not empty")
    private String username;
    @NotBlank(message = "password is not empty")
    private String password;
}
