package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private DashboardRepository dashboardRepository;
    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }


    public List<UserEntityRoleDto> getUserEntityRoleListOnUsername(String username) {
        return dashboardRepository.getUserEntityRoleListOnUsername(username);
    }

    public int addProject(Project project){
        return dashboardRepository.addProject(project);
    }
}
