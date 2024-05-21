package dev.tolana.projectcalculationtool.exception.organisation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class OrganisationExceptionHandler {

    @ExceptionHandler(OrganisationWarningException.class)
    public String organisationWarningException(OrganisationWarningException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("alertWarning",ex.getMessage());
        return "redirect:" + referer;
    }

    @ExceptionHandler(OrganisationDangerException.class)
    public String organisationDangerException(OrganisationWarningException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("alertDanger",ex.getMessage());
        return "redirect:" + referer;
    }
}
