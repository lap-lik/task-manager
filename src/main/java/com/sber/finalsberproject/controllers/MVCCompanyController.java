package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.AddUserDTO;
import com.sber.finalsberproject.dto.CompanyDTO;
import com.sber.finalsberproject.dto.UserDTO;
import com.sber.finalsberproject.exception.MyDeleteException;
import com.sber.finalsberproject.service.CompanyService;
import com.sber.finalsberproject.service.UserService;
import com.sber.finalsberproject.service.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.sber.finalsberproject.constants.UserRolesConstants.ADMIN;

@Slf4j
@Controller
@RequestMapping("/company")
public class MVCCompanyController {
    private final CompanyService companyService;
    private final UserService userService;

    public MVCCompanyController(CompanyService companyService,
                                UserService userService) {
        this.companyService = companyService;
        this.userService = userService;
    }

    private CustomUserDetails myCustomUserDetails() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("")
    public String getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "size", defaultValue = "10") int pageSize,
                         Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "title"));
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<CompanyDTO> result;
        if (ADMIN.equalsIgnoreCase(userName)) {
            result = companyService.listAll(pageRequest);
        } else {
            result = companyService.listAllNotDeleted(pageRequest);
        }
        model.addAttribute("theCompany", result);
        return "company/view-all";
    }

    @GetMapping("/add")
    public String create() {
        return "company/add";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("companyForm") CompanyDTO companyDTO) {
        companyService.create(companyDTO);
        return "redirect:/company";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         Model model) {
        model.addAttribute("company", companyService.getOne(id));
        return "/company/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("companyForm") CompanyDTO companyDTO) {
        CompanyDTO company = companyService.getOne(companyDTO.getId());
        company.setTitle(companyDTO.getTitle());
        companyService.update(company);
        return "redirect:/company";
    }
    @GetMapping("/my")
    public String getMy(Model model) {
            UserDTO userDTO = userService.getOne(Long.valueOf(myCustomUserDetails().getUserId()));
           return getOne(userDTO.getCompanyId(), model);
    }

    @GetMapping("/{id}")
    public String getOne(@PathVariable Long id,
                         Model model) {
        model.addAttribute("company", companyService.getOne(id));
        model.addAttribute("users1", userService.listAllByRole(1L));
        model.addAttribute("users2", userService.listAllByRole(2L));
        model.addAttribute("users", userService.listAll());
        model.addAttribute("companyId", id);
        return "company/view-one";
    }

    @PostMapping("/add-user")
    public String addUserByCompany(@ModelAttribute("companyAddUserForm") AddUserDTO addUserDTO) {
        userService.addCompany(addUserDTO);
        return "redirect:/company/" + addUserDTO.getCompanyId();
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) throws MyDeleteException {
        companyService.deleteSoft(id);
        return "redirect:/company";
    }

    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        companyService.restore(id);
        return "redirect:/company";
    }

    @PostMapping("/search")
    public String searchCompany(@RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "5") int pageSize,
                                @ModelAttribute("companySearchForm") CompanyDTO companyDTO,
                                Model model) {
        if (StringUtils.hasText(companyDTO.getTitle()) || StringUtils.hasLength(companyDTO.getTitle())) {
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "title"));
            model.addAttribute("theCompany", companyService.searchCompany(companyDTO.getTitle().trim(), pageRequest));
            return "company/view-all";
        } else {
            return "redirect:/company";
        }
    }
}
