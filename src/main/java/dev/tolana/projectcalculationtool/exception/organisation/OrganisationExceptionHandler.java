package dev.tolana.projectcalculationtool.exception.organisation;

import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.exception.EntityExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class OrganisationExceptionHandler{

    @ExceptionHandler(OrganisationWarningException.class)
    public String handle(EntityException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("referer");
        switch(ex.getAlert()) {
            case WARNING:
                redirectAttributes.addFlashAttribute("alertWarning", ex.getMessage());
            case DANGER:
                redirectAttributes.addFlashAttribute("alertDanger", ex.getMessage());
        }
        return "redirect:" + referer;
    }

}
