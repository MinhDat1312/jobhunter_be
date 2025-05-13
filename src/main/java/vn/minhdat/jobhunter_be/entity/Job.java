package vn.minhdat.jobhunter_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import vn.minhdat.jobhunter_be.common.Level;
import vn.minhdat.jobhunter_be.common.WorkingType;
import vn.minhdat.jobhunter_be.util.SecurityUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long jobId;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    private Level level;
    private int quantity;
    private double salary;
    private String title;
    private WorkingType workingType;
    private String location;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @JsonIgnore
    private List<Skill> skills;

    @ManyToOne
    @JoinColumn(name = "career_id")
    private Career career;

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
