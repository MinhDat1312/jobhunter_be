package vn.minhdat.jobhunter_be.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.minhdat.jobhunter_be.common.Gender;
import vn.minhdat.jobhunter_be.entity.embeddable.Address;
import vn.minhdat.jobhunter_be.entity.embeddable.Contact;
import vn.minhdat.jobhunter_be.util.SecurityUtil;
import vn.minhdat.jobhunter_be.util.annotation.RequireAddressIfRecruiter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RequireAddressIfRecruiter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Recruiter.class, name = "recruiter"),
        @JsonSubTypes.Type(value = Applicant.class, name = "applicant")
})
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long userId;
    @Embedded
    protected Address address;
    @Embedded
    @Valid
    @NotNull(message = "Contact is not empty")
    protected Contact contact;
    protected LocalDate dob;
    protected String fullName;
    @Enumerated(EnumType.STRING)
    protected Gender gender;
    @NotBlank(message = "Password is not empty")
    protected String password;
    @Column(columnDefinition = "MEDIUMTEXT")
    protected String refreshToken;
    @NotBlank(message = "Username is not empty")
    protected String username;

    protected Instant createdAt;
    protected String createdBy;
    protected Instant updatedAt;
    protected String updatedBy;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void handleBeforeCreate(){
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }
    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }
}
