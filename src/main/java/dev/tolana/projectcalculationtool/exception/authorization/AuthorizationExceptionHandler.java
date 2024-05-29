package dev.tolana.projectcalculationtool.exception.authorization;


import dev.tolana.projectcalculationtool.dto.BreadCrumbDto;
import dev.tolana.projectcalculationtool.service.BreadCrumbService;
import dev.tolana.projectcalculationtool.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

@ControllerAdvice
public class AuthorizationExceptionHandler {
//    this exception is thrown by JdbcUserDetailsManager
//    which is the only class that will ever throw it.
//    we know it's a 'username already exists' error, and send that back.

    private UserService userService;
    private BreadCrumbService breadCrumbService;

    public AuthorizationExceptionHandler(UserService userService, BreadCrumbService breadCrumbService) {
        this.userService = userService;
        this.breadCrumbService = breadCrumbService;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(HttpServletRequest req, RedirectAttributes redirectAttributes) {
        String referer = req.getHeader("referer");
        redirectAttributes.addFlashAttribute("alertDanger", "Du har ikke tilladelse til dette!");
        if (referer == null) {
            return "redirect:/dashboard";
        }
        return "redirect:" + referer;
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

    @ModelAttribute("breadCrumb")
    public BreadCrumbDto getBreadCrumb(HttpServletRequest request) {
        return breadCrumbService.getBreadCrumb(request);
    }
}
