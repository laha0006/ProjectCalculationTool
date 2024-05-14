package dev.tolana.projectcalculationtool.enums;

public enum Status {
    IN_PROGRESS(1,"I gang"),
    DONE(2,"Afsluttet"),
    IN_REVIEW(3,"Til gennemsyn");

    private final long id;
    private final String text;
    Status(long id, String text) {
        this.text = text;
        this.id = id;
    }
    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

}
