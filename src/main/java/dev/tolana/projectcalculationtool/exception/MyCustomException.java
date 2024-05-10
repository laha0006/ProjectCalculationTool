package dev.tolana.projectcalculationtool.exception;

import java.sql.SQLException;

public class MyCustomException extends SQLException {
    public MyCustomException(String reason) {
        super(reason);
    }
}
