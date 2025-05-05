package vn.minhdat.jobhunter_be.common;

public enum Gender {
    MALE("Nam"), FEMALE("Nữ"), OTHER("Khác");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
