package dev.tolana.projectcalculationtool.exception;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLException.class)
    public String sqlException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        return "user/dashboard";
    }
}
