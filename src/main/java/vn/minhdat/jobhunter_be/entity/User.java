package vn.minhdat.jobhunter_be.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.minhdat.jobhunter_be.common.Gender;
import vn.minhdat.jobhunter_be.entity.embeddable.Address;
import vn.minhdat.jobhunter_be.entity.embeddable.Contact;

import java.time.Instant;
import java.time.LocalDate;

@Table(name = "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    @Embedded
    private Address address;
    @Embedded
    @Valid
    @NotNull(message = "Contact is not empty")
    private Contact contact;
    private LocalDate dob;
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotBlank(message = "Password is not empty")
    private String password;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    @NotBlank(message = "Username is not empty")
    private String username;


    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate(){
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedAt = Instant.now();
    }
}
