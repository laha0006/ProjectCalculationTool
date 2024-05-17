package dev.tolana.projectcalculationtool.model;

public class Invitation {
    private String username;
    private long organsiationId;

    public Invitation(String username, long organsiationId) {
        this.username = username;
        this.organsiationId = organsiationId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public long getOrgansiationId() {
        return organsiationId;
    }
    public void setOrgansiationId(long organsiationId) {
        this.organsiationId = organsiationId;
    }
}
