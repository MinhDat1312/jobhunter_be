package vn.minhdat.jobhunter_be.common;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("Đang xét"), ACCEPTED("Chấp nhận"), REJECTED("Từ chối");

    private final String value;

    Status(String value) {
        this.value = value;
    }
}
