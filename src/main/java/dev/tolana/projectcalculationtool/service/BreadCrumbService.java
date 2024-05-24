package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.BreadCrumbDto;
import dev.tolana.projectcalculationtool.dto.BreadCrumbItmDto;
import dev.tolana.projectcalculationtool.dto.NameHierarchy;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.repository.BreadCrumbRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BreadCrumbService {

    private BreadCrumbRepository breadCrumbRepository;

    public BreadCrumbService(BreadCrumbRepository breadCrumbRepository) {
        this.breadCrumbRepository = breadCrumbRepository;
    }


    private List<BreadCrumbItmDto> createItems(NameHierarchy nameHierarchy, String url) {
        List<BreadCrumbItmDto> items = new ArrayList<>();
        return items;
    }

    private BreadCrumbDto createBreadCrumb(String url) {
        String strippedUrl = url.substring(1);
        String[] tokens = strippedUrl.split("/");
        int length = tokens.length;
        NameHierarchy nameHierarchy = switch (length) {
            case 2 -> breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[1]), EntityType.ORGANISATION);
            case 4 -> breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[3]), EntityType.DEPARTMENT);
            case 6 -> breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[5]), EntityType.TEAM);
            case 8 -> breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[7]), EntityType.PROJECT);
            case 10 -> breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[9]), EntityType.TASK);
            default -> null;
        };
        List<BreadCrumbItmDto> items = createItems(nameHierarchy, url);
        return null;
    }


    public BreadCrumbDto getBreadCrumb(HttpServletRequest request) {
        String url = request.getRequestURI();
        if (url.startsWith("/organisation/") && !url.contains("create")) {
            return createBreadCrumb(url);
        }
        return new BreadCrumbDto(false, null);
    }
}
