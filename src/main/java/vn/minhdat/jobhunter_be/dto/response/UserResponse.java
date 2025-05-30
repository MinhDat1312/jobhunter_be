package vn.minhdat.jobhunter_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.entity.embeddable.Address;
import vn.minhdat.jobhunter_be.entity.embeddable.Contact;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long userId;
    private Address address;
    private Contact contact;
    private String username;
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
