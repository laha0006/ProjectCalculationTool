package dev.tolana.projectcalculationtool.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface EntityExceptionHandler {
    String handleException(EntityException e, HttpServletRequest request, RedirectAttributes redirectAttributes);
}
