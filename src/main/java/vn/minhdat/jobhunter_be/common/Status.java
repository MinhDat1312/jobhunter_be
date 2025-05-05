package vn.minhdat.jobhunter_be.common;

public enum Status {
    PENDING("Đang xét"), ACCEPTED("Chấp nhận"), REJECTED("Từ chối");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
