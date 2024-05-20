package dev.tolana.projectcalculationtool.exception;


import dev.tolana.projectcalculationtool.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {
//    this exception is thrown by JdbcUserDetailsManager
//    which is the only class that will ever throw it.
//    we know it's a 'username already exists' error, and send that back.

    private UserService userService;

    public GlobalExceptionHandler(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public String sqlException(SQLException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("alertWarning", "username already exists!");
        return "redirect:/user/register";
    }

    @ExceptionHandler(InviteFailureException.class)
    public String inviteFailureException(InviteFailureException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("alertWarning", ex.getMessage());
        return "redirect:" + referer;
    }

    @ExceptionHandler(UserAlreadyInOrganisationException.class)
    public String userAlreadyInOrganisation(UserAlreadyInOrganisationException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("alertWarning", "Brugeren er allerede i din organisation!");
        return "redirect:" + referer;
    }

    @ModelAttribute("inviteCount")
    public int getInviteCount(Authentication authentication) {
        if (authentication == null) {
            return 0;
        }
        String username = authentication.getName();
        return userService.getInvitationsCount(username);
    }
}
