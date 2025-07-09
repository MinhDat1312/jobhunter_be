package vn.minhdat.jobhunter_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.common.Gender;
import vn.minhdat.jobhunter_be.entity.Job;
import vn.minhdat.jobhunter_be.entity.embeddable.Contact;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private long userId;
    private String address;
    private Contact contact;
    private String username;
    private String fullName;
    private String avatar;
    private Gender gender;
    private LocalDate dob;
    private RoleUser role;
    private List<SavedJob> savedJobs;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleUser {
        private long roleId;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavedJob {
        private long jobId;
        private String title;
    }
}
