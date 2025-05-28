package vn.minhdat.jobhunter_be.common;

import lombok.Getter;

@Getter
public enum WorkingType {
    FULLTIME("Full time"), PARTTIME("Part time"), ONLINE("Online"), OFFLINE("Offline");

    private final String value;

    WorkingType(String value) {
        this.value = value;
    }
}
