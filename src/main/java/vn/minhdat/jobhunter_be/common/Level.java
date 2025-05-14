package vn.minhdat.jobhunter_be.common;

public enum Level {
    FRESHER("Fresher"), JUNIOR("Junior"), SENIOR("Senior"),
    INTERN("Intern"), MIDDLE("Middle");

    private final String value;

    Level(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
