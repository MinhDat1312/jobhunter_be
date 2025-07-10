package vn.minhdat.jobhunter_be.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.entity.Role;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private long userId;
        private String email;
        private String fullName;
        private String username;
        private String avatar;
        private String type;
        private Role role;
        private List<Job> savedJobs;
        private List<Recruiter> followedRecruiters;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInsideToken {
        private long userId;
        private String email;
        private String fullName;
    }
}
