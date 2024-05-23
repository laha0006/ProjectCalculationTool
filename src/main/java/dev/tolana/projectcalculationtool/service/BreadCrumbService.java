package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.NameHierarchy;
import dev.tolana.projectcalculationtool.repository.BreadCrumbRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class BreadCrumbService {

    private BreadCrumbRepository breadCrumbRepository;

    public BreadCrumbService(BreadCrumbRepository breadCrumbRepository) {
        this.breadCrumbRepository = breadCrumbRepository;
    }


    public String getBreadCrumb(HttpServletRequest request) {
        String url = request.getRequestURI();
        System.out.println("### URL: " + url);
        if (!url.startsWith("/organisation/")) {
            return null;
        }
        NameHierarchy nameHierarchy = breadCrumbRepository.getNameHierarchy();
        return "breadCrumb yea..."
    }
}
