package dev.tolana.projectcalculationtool.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class EntityExceptionHandlerImpl implements EntityExceptionHandler {

    @Override
    @ExceptionHandler(EntityException.class)
    public String handleException(EntityException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("referer");
        switch(ex.getAlert()) {
            case WARNING:
                redirectAttributes.addFlashAttribute("alertWarning", ex.getMessage());
            case DANGER:
                redirectAttributes.addFlashAttribute("alertDanger", ex.getMessage());
        }
        if(referer == null) {
            return "redirect:/dashboard";
        }
        return "redirect:" + referer;
    }

}
