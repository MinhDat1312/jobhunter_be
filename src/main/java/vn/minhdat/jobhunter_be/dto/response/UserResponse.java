package vn.minhdat.jobhunter_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.common.Gender;
import vn.minhdat.jobhunter_be.entity.embeddable.Contact;

import java.time.Instant;
import java.time.LocalDate;

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
    private Gender gender;
    private LocalDate dob;
    private Instant createdAt;
    private Instant updatedAt;
    private RoleUser role;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleUser {
        private long roleId;
        private String name;
    }
}
