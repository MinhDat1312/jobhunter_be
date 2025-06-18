package vn.minhdat.jobhunter_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.common.Education;
import vn.minhdat.jobhunter_be.common.Level;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantResponse extends UserResponse{
    private boolean availableStatus;
    private Education education;
    private Level level;
}
