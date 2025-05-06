package vn.minhdat.jobhunter_be.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.minhdat.jobhunter_be.common.Education;
import vn.minhdat.jobhunter_be.common.Level;

@Entity
@Table(name = "applicants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Applicant extends User{
    private boolean availableStatus = true;
    @Enumerated(EnumType.STRING)
    private Education education;
    @Enumerated(EnumType.STRING)
    private Level level;
    private String resumeUrl;
}
