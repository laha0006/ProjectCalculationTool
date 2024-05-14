package dev.tolana.projectcalculationtool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organisation/{org_id}/department")
public class DepartmentController {

    @GetMapping("/{dept_id}")
    public String department(@PathVariable int dept_id,@PathVariable int org_id, Model model) {
        return "department/departmentView";
    }
}
