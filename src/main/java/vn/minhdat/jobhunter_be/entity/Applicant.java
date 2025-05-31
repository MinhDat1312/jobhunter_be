package vn.minhdat.jobhunter_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import vn.minhdat.jobhunter_be.common.Education;
import vn.minhdat.jobhunter_be.common.Level;

import java.util.List;

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

    @OneToMany(mappedBy = "applicant", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Application> applications;
}
