package vn.minhdat.jobhunter_be.common;

public enum Education {
    COLLEGE("Cao đẳng"), UNIVERSITY("Đại học"), SCHOOL("THPT"), ENGINEER("Kỹ sư");

    private final String value;

    Education(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
