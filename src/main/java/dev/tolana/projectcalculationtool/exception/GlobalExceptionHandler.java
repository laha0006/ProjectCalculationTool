package dev.tolana.projectcalculationtool.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {
//    this exception is thrown by JdbcUserDetailsManager
//    which is the only class that will ever throw it.
//    we know it's a 'username already exists' error, and send that back.
    @ExceptionHandler(DuplicateKeyException.class)
    public String sqlException(SQLException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "username already exsits!");
        return "redirect:/user/register";
    }

    @ExceptionHandler(MyCustomException.class)
    public String myCustomException(MyCustomException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        System.out.println("MyCustomException: " + ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}
