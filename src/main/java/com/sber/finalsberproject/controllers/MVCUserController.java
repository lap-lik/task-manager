package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.constants.Errors;
import com.sber.finalsberproject.dto.UserDTO;
import com.sber.finalsberproject.exception.MyDeleteException;
import com.sber.finalsberproject.service.UserService;
import com.sber.finalsberproject.service.userdetails.CustomUserDetails;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

import static com.sber.finalsberproject.constants.UserRolesConstants.ADMIN;
@Slf4j
@Controller
@RequestMapping("/users")
public class MVCUserController {
    private final UserService userService;
    public MVCUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(Model model){
        model.addAttribute("userForm", new UserDTO());
        return "registration";
    }
    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") UserDTO userDTO,
                               BindingResult bindingResult,
                               MultipartFile file){
        if (userDTO.getLogin().equalsIgnoreCase(ADMIN) || userService.getUserByLogin(userDTO.getLogin()) != null) {
            bindingResult.rejectValue("login", "error.login", "Такой логин уже существует");
            return "registration";
        }
        if (userService.getUserByEmail(userDTO.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.email", "Такой e-mail уже существует");
            return "registration";
        }
        if (file != null && file.getSize() > 0) {
            userService.create(userDTO, file);
        } else {
            userService.create(userDTO);
        }
        return "/login";
    }

    @GetMapping("/profile/{id}")
    public String userProfile(@PathVariable Integer id,
                              @ModelAttribute(value = "exception") String exception,
                              Model model) throws AuthException {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.isNull(customUserDetails.getUserId())) {
            if (!ADMIN.equalsIgnoreCase(customUserDetails.getUsername())) {
                if (!id.equals(customUserDetails.getUserId())) {
                    throw new AuthException(HttpStatus.FORBIDDEN + ": " + Errors.Users.USER_FORBIDDEN_ERROR);
                }
            }
        }
        model.addAttribute("user", userService.getOne(Long.valueOf(id)));
        model.addAttribute("exception", exception);
        return "profile/profile-view";
    }
    @GetMapping("/profile/update/{id}")
    public String updateProfile(@PathVariable Integer id,
                                Model model) throws AuthException {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!id.equals(customUserDetails.getUserId())) {
            throw new AuthException(HttpStatus.FORBIDDEN + ": " + Errors.Users.USER_FORBIDDEN_ERROR);
        }
        model.addAttribute("userForm", userService.getOne(Long.valueOf(id)));
        return "profile/profile-update";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("userForm") UserDTO userDTOFromUpdateForm,
                                BindingResult bindingResult,
                                @RequestParam(value = "onlineCopyPath", required = false) MultipartFile file) {
        UserDTO userEmailDuplicated = userService.getUserByEmail(userDTOFromUpdateForm.getEmail());
        UserDTO foundUser = userService.getOne(userDTOFromUpdateForm.getId());
        if (userEmailDuplicated != null && !Objects.equals(userEmailDuplicated.getEmail(), foundUser.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Такой email уже существует");
            return "profile/profile-update";
        }
        foundUser.setFirstName(userDTOFromUpdateForm.getFirstName());
        foundUser.setLastName(userDTOFromUpdateForm.getLastName());
        foundUser.setMiddleName(userDTOFromUpdateForm.getMiddleName());
        foundUser.setEmail(userDTOFromUpdateForm.getEmail());
        foundUser.setBirthDate(userDTOFromUpdateForm.getBirthDate());
        foundUser.setMobilePhone(userDTOFromUpdateForm.getMobilePhone());
        foundUser.setAddress(userDTOFromUpdateForm.getAddress());
        if (file != null && file.getSize() > 0) {
            userService.update(foundUser, file);
        } else {
            userService.update(foundUser);
        }
        return "redirect:/users/profile/" + userDTOFromUpdateForm.getId();
    }

    @GetMapping("/view-all")
    public String listAllUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "5") int pageSize,
                               Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "login"));
        Page<UserDTO> userPage = userService.listAll(pageRequest);
        model.addAttribute("users", userPage);
        return "users/view-all";
    }

    @GetMapping("/add-administrator")
    public String addAdministrator(Model model) {
        model.addAttribute("userForm", new UserDTO());
        return "users/add-administrator";
    }

    @PostMapping("/add-administrator")
    public String addAdministrator(@ModelAttribute("userForm") UserDTO userDTO,
                               BindingResult bindingResult) {
        if (userDTO.getLogin().equalsIgnoreCase(ADMIN) || userService.getUserByLogin(userDTO.getLogin()) != null) {
            bindingResult.rejectValue("login", "error.login", "Такой логин уже существует");
            return "registration";
        }
        if (userService.getUserByEmail(userDTO.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.email", "Такой email уже существует");
            return "registration";
        }
        userService.createAdministrator(userDTO);
        return "redirect:/users/view-all";
    }
    @PostMapping("/view-all/search")
    public String searchUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              @ModelAttribute("userSearchForm") UserDTO userDTO,
                              Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "first_name"));
        model.addAttribute("users", userService.findUsers(userDTO, pageRequest));
        return "users/view-all";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) throws MyDeleteException {
        userService.deleteSoft(id);
        return "redirect:/users/view-all";
    }

    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        userService.restore(id);
        return "redirect:/users/view-all";
    }

    @GetMapping("/dismiss/{id}/{companyId}")
    public String dismiss(@PathVariable Long id, @PathVariable Long companyId) throws MyDeleteException {
        userService.dismissUser(id);
        return "redirect:/company/" + companyId;
    }

    @ExceptionHandler({AuthException.class})
    public RedirectView handleError(HttpServletRequest request,
                                    Exception ex,
                                    RedirectAttributes redirectAttributes) {
        log.error("Запрос " + request.getRequestURL() + " вызвал ошибку " + ex.getMessage());
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return new RedirectView("/users/profile/" + customUserDetails.getUserId(), true);
    }
}
