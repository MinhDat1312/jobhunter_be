package vn.minhdat.jobhunter_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.common.Level;
import vn.minhdat.jobhunter_be.common.WorkingType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {
    private long jobId;
    private String title;
    private String location;
    private double salary;
    private int quantity;
    private Level level;
    private WorkingType workingType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private List<String> skills;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}
