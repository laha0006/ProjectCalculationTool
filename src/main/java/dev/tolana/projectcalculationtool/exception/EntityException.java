package dev.tolana.projectcalculationtool.exception;

import dev.tolana.projectcalculationtool.enums.Alert;

public class EntityException extends RuntimeException{
    private final Alert alert;
    public EntityException(String message, Alert alert) {
        super(message);
        this.alert = alert;
    }
    public Alert getAlert() {
        return alert;
    }
}
