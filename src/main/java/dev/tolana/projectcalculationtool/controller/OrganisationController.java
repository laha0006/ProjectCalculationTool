package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.CreateOrganisationFormDto;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.service.OrganisationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/organisation")
public class OrganisationController {

    private final OrganisationService organisationService;

    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    @GetMapping("")
    public String organisationMainPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Organisation> listOfUserOrgs = organisationService.getNotArchivedOrganisationsByUser(username);
        model.addAttribute("allUserOrgs", listOfUserOrgs);

        return "organisation/userOrganisations";
    }

    @GetMapping("/{id}")
    public String organisationPage(@PathVariable long id, Model model, Authentication authentication) {
        Organisation organisation = organisationService.getOrganisationsById(id);
        model.addAttribute("organisation", organisation);
        return "organisation/organisationView";
    }

    @GetMapping("/create")
    public String createOrganisation(Model model) {
        CreateOrganisationFormDto emptyOrganisationDto = new CreateOrganisationFormDto("", "");
        model.addAttribute("organisation", emptyOrganisationDto);

        return "organisation/createOrganisation";
    }

    @PostMapping("/create")
    public String createOrganisation(@ModelAttribute CreateOrganisationFormDto organisationDto, Authentication authentication) {
        String username = authentication.getName();
        String orgName = organisationDto.orgName();
        String orgDescription = organisationDto.orgDescription();
        organisationService.createOrganisation(username, orgName, orgDescription);
        return "redirect:/organisation";
    }

}
