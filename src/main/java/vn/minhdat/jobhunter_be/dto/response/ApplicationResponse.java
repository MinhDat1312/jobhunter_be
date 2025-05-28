package vn.minhdat.jobhunter_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.common.Status;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {
    private long id;
    private String email;
    private String url;
    private String recruiterName;
    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    private UserApplication user;
    private JobApplication job;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserApplication {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobApplication {
        private long id;
        private String name;
    }
}
