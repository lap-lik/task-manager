package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.RoleDTO;
import com.sber.finalsberproject.dto.UserDTO;
import com.sber.finalsberproject.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@Slf4j
@Transactional
@Rollback(value = false)
public class MVCUserControllerTest extends CommonTestMVC {
    @Autowired
    private UserService userService;
    UserDTO userDTO = new UserDTO("Login_test", "1", "1@1.com", LocalDate.now(),
            "Name_test", "Last_Name", "Middle_Name", "8-900-800-70-60", "A:/1/1",
            "Moscow", false, LocalDate.now(), new RoleDTO(1L, "USER", "Роль пользователя"), new ArrayList<>(), 1L);
    UserDTO userDTOUpdate = new UserDTO("Login_test_update", "1", "1@1.com", LocalDate.now(),
            "Name_test", "Last_Name", "Middle_Name", "8-900-800-70-60", "A:/1/1",
            "Moscow", false, LocalDate.now(), new RoleDTO(1L, "USER", "Роль пользователя"), new ArrayList<>(), 1L);

    @Test
    @Order(0)
    @Override
    @DisplayName("Тест просмотр всех пользователей")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void listAll() throws Exception {
        log.info("Тест просмотр всех пользователей начат!");
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/users/view-all")
                        .param("page", "1")
                        .param("size", "25")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("users/view-all"))
                .andExpect(model().attributeExists("users"))
                .andReturn();
        log.info("Тест просмотр всех пользователей закончен!");
    }

    @Test
    @Override
    @Order(1)
    @DisplayName("Тест регистрации пользователя")
    @WithAnonymousUser
    protected void createObject() throws Exception {
        log.info("Тест регистрации пользователя начат!");
        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("userForm", userDTO)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(redirectedUrlTemplate("/login"))
                .andExpect(redirectedUrl("/login"));
        log.info("Тест регистрации пользователя закончен!");
    }

    @Test
    @Order(2)
    @DisplayName("Тест редактирования пользователя")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void updateObject() throws Exception {
        log.info("Тест редактирования пользователя начат!");
        UserDTO user = userService.getOne(userDTO.getId());
        user.setFirstName(userDTOUpdate.getFirstName());
        mvc.perform(post("/profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .flashAttr("userForm", userDTOUpdate)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/profile/" + user.getId()))
                .andExpect(redirectedUrl("/users/profile/" + user.getId()));
        log.info("Тест редактирования пользователя начат!");
    }

    @Test
    @Order(3)
    @Override
    @DisplayName("Тест по софт удалению пользователя")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void deleteObject() throws Exception {
        log.info("Тест по софт удалению пользователя начат!");
        UserDTO user = userService.getOne(userDTOUpdate.getId());
        user.setDeleted(true);
        mvc.perform(get("/users/delete/{id}", user.getId())
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect: users/view-all"))
                .andExpect(redirectedUrl("/users/view-all"));

        UserDTO deletedUser = userService.getOne(userDTOUpdate.getId());
        assertTrue(deletedUser.isDeleted());
        log.info("Тест по софт удалению пользователя закончен!");
    }

    @Test
    @Order(4)
    @DisplayName("Тест по снятию мягкого удаления пользователя")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void restore() throws Exception {
        log.info("Тест по снятию мягкого удаления пользователя начат!");
        UserDTO user = userService.getOne(userDTOUpdate.getId());
        user.setDeleted(false);
        mvc.perform(get("/users/restore/{id}", user.getId())
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect: users/view-all"))
                .andExpect(redirectedUrl("/users/view-all"));

        UserDTO deletedUser = userService.getOne(userDTOUpdate.getId());
        assertFalse(deletedUser.isDeleted());
        log.info("Тест по снятию мягкого удаления пользователя закончен!");
    }

    @Test
    @Order(5)
    @DisplayName("Тест по увольнению пользователя")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void dismiss() throws Exception {
        log.info("Тест по увольнению пользователя начат!");
        UserDTO user = userService.getOne(userDTOUpdate.getId());
        mvc.perform(get("/users/dismiss/{id}/{companyId}", user.getId(), user.getCompanyId())
                        .param("id", user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect: company/" + user.getCompanyId()))
                .andExpect(redirectedUrl("/company/" + user.getCompanyId()));
        log.info("Тест по увольнению пользователя закончен!");
    }
}
