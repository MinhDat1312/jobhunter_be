package vn.minhdat.jobhunter_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.minhdat.jobhunter_be.entity.Job;

import java.util.List;

@Getter
@Setter
public class SaveJobRequest {
    @NotNull(message = "user is not empty")
    private Long userId;
    @NotNull(message = "Saved jobs must not be null")
    private List<Job> savedJobs;
}
