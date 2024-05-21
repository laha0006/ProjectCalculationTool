package dev.tolana.projectcalculationtool.exception.organisation;

import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.exception.EntityException;

public class OrganisationException extends EntityException {
    public OrganisationException(String message, Alert alert) {
        super(message, alert);
    }
}
