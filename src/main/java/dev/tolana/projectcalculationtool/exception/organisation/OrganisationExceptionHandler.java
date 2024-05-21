package dev.tolana.projectcalculationtool.exception.organisation;

import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.exception.EntityExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class OrganisationExceptionHandler implements EntityExceptionHandler {

    @Override
    @ExceptionHandler(OrganisationWarningException.class)
    public String warningException(EntityException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("alertWarning",ex.getMessage());
        return "redirect:" + referer;
    }
    @Override
    @ExceptionHandler(OrganisationDangerException.class)
    public String dangerException(EntityException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("alertDanger",ex.getMessage());
        return "redirect:" + referer;
    }
}
