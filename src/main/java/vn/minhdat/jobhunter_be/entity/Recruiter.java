package vn.minhdat.jobhunter_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "recruiters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recruiter extends User{
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String logo;
    private String website;
}
