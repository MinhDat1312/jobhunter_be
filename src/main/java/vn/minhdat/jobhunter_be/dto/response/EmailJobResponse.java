package vn.minhdat.jobhunter_be.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class EmailJobResponse {
    private String title;
    private double salary;
    private RecruiterEmail recruiter;
    private List<SkillEmail> skills;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RecruiterEmail {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SkillEmail {
        private String name;
    }
}
