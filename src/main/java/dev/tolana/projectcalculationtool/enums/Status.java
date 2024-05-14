package dev.tolana.projectcalculationtool.enums;

public enum Status {
    IN_PROGRESS("I gang"),
    DONE("Afsluttet"),
    IN_REVIEW("Til gennemsyn");

    private final String text;
    Status(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }

}
