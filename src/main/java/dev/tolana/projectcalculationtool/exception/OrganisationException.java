package dev.tolana.projectcalculationtool.exception;

import dev.tolana.projectcalculationtool.enums.Alert;

public class OrganisationException extends EntityException {
    public OrganisationException(String message, Alert alert) {
        super(message, alert);
    }}
