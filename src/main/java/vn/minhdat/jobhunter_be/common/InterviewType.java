package vn.minhdat.jobhunter_be.common;

public enum InterviewType {
    PERSON("Trực tiếp"), VIDEO("Video"), PHONE("Điện thoại"), TECHNICAL("Kỹ thuật");

    private final String value;

    InterviewType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
