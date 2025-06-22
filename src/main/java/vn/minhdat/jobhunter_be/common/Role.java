package vn.minhdat.jobhunter_be.common;

import lombok.Getter;

@Getter
public enum Role {

    ADMIN("SUPER_ADMIN"), RECRUITER("HR"), APPLICANT("APPLICANT");

    private String value;

    Role(String value) {
        this.value = value;
    }
}
