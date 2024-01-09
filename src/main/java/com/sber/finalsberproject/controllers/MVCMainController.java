package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.TaskDTO;
import com.sber.finalsberproject.service.TaskService;
import com.sber.finalsberproject.service.UserService;
import com.sber.finalsberproject.service.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
public class MVCMainController {
    private final TaskService taskService;
    private final UserService userService;

    public MVCMainController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/other")
    public String index(@ModelAttribute(name = "exception") final String exception,
                         Model model) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (customUserDetails.getUsername().equalsIgnoreCase("admin")) {
            return "index";
        }
        model.addAttribute("usersCompany", userService.listAllByCompany(userService.getOne(customUserDetails.getUserId().longValue()).getCompanyId()));
        if (!Objects.isNull(customUserDetails)) {
            if ("[ROLE_ADMINISTRATOR]".equalsIgnoreCase(customUserDetails.getAuthorities().toString())) {
                List<TaskDTO> result1 = taskService.listAllByPatternForMain(0);
                List<TaskDTO> result2 = taskService.listAllByPatternForMain(1);
                List<TaskDTO> result3 = taskService.listAllByPatternForMain(2);
                model.addAttribute("bigTasks", result1);
                model.addAttribute("mediumTasks", result2);
                model.addAttribute("smallTasks", result3);
            } else {
                List<TaskDTO> result1 = taskService.getListByUserIdForMain(customUserDetails.getUserId().longValue(), 0);
                List<TaskDTO> result2 = taskService.getListByUserIdForMain(customUserDetails.getUserId().longValue(), 1);
                List<TaskDTO> result3 = taskService.getListByUserIdForMain(customUserDetails.getUserId().longValue(), 2);
                model.addAttribute("bigTasks", result1);
                model.addAttribute("mediumTasks", result2);
                model.addAttribute("smallTasks", result3);
            }
            return "index";
        }
        return "/layoutFragments/main";
    }
}
