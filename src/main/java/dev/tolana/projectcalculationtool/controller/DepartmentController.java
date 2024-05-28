package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.service.DepartmentService;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/organisation/{orgId}/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/{deptId}")
    public String viewDepartment(@PathVariable("deptId") long departmentId , Model model) {
        EntityViewDto department = departmentService.getDepartment(departmentId);
        model.addAttribute("department", department);

        List<EntityViewDto> teams = departmentService.getChildren(departmentId);
        model.addAttribute("allTeams", teams);

        return "department/departmentView";
    }

    @GetMapping("{deptId}/members")
    public String organisationMembersView(@PathVariable("orgId") long orgId,
            @PathVariable("deptId") long departmentId, Model model){
        //TODO exclude owner of department from results
        EntityViewDto department = departmentService.getDepartment(departmentId);
        model.addAttribute("department", department);

        EntityViewDto organisation = departmentService.getParent(orgId);
        model.addAttribute("organisation", organisation);

        List<UserEntityRoleDto> users = departmentService.getUsersFromOrganisationId(
                            organisation.id(),departmentId);

        model.addAttribute("deptUsers",users);

        return "department/viewMembers";
    }

    @GetMapping("/create")
    public String createDepartment(Model model, @PathVariable long orgId) {
        EntityCreationDto emptyDepartmentDto = new EntityCreationDto("", "", orgId, EntityType.DEPARTMENT);
        model.addAttribute("department", emptyDepartmentDto);
        return "department/createDepartment";
    }

    @PostMapping("/create")
    public String createDepartment(@ModelAttribute EntityCreationDto departmentDto, RedirectAttributes redirectAttributes, Authentication authentication) {
        departmentService.createDepartment(departmentDto, authentication.getName());
        redirectAttributes.addFlashAttribute("alertSuccess", "Department created successfully");
        return "redirect:/organisation/" + departmentDto.parentId();
    }

    @PostMapping("/{deptId}/delete")
    public String deleteDepartment(@PathVariable long orgId,
                                   @PathVariable long deptId) {

        departmentService.deleteDepartment(deptId);
        return "redirect:/organisation/" + orgId;
    }

    @GetMapping("/{deptId}/edit")
    public String editDepartment(@PathVariable("deptId") long departmentId , Model model) {
        EntityEditDto department = departmentService.getDepartmentToEdit(departmentId);
        model.addAttribute("department", department);

        return "department/editDepartment";
    }

    @PostMapping("/{deptId}/edit")
    public String editOrganisation(@ModelAttribute EntityEditDto editDto) {

        departmentService.editDepartment(editDto);
        return "redirect:../{deptId}";
    }

    @PostMapping("/{deptId}/members/assign/{username}")
    public String assignMemberToDepartment(@PathVariable("orgId") long orgId,
                                           @PathVariable("deptId") long deptId,
                                           @PathVariable("username") String username){

        departmentService.assignMemberToDepartment(deptId,username);


        return "redirect:/organisation/" + orgId + "/department/"+ deptId +"/members";
    }

    @PostMapping("/{deptId}/members/promote/{username}")
    public String promoteMemberToAdmin(@PathVariable("orgId") long orgId,
                                       @PathVariable("deptId") long deptId,
                                       @PathVariable("username") String username){


        departmentService.promoteMemberToAdmin(deptId,username);


        return "redirect:/organisation/" + orgId + "/department/"+ deptId +"/members";
    }

    @PostMapping("/{deptId}/members/kick/{username}")
    public String kickMemberFromDepartment(@PathVariable("orgId") long orgId,
                                           @PathVariable("deptId") long deptId,
                                           @PathVariable("username") String username){



        departmentService.kickMemberFromDepartment(deptId,username);


        return "redirect:/organisation/" + orgId + "/department/"+ deptId +"/members";
    }
}
