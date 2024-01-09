package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.CompanyDTO;
import com.sber.finalsberproject.service.CompanyService;
import com.sber.finalsberproject.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@Rollback(value = false)
public class MVCCompanyControllerTest extends CommonTestMVC {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;
    CompanyDTO companyDTO = new CompanyDTO("Test_company", new ArrayList<>());
    CompanyDTO companyDTOUpdate = new CompanyDTO("Test_company_update", new ArrayList<>());

    @Test
    @Order(0)
    @Override
    @DisplayName("Тест просмотр всех фирм")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void listAll() throws Exception {
        log.info("Тест просмотр всех фирм начат!");
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/company")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("company/view-all"))
                .andExpect(model().attributeExists("theCompany"))
                .andReturn();

        log.info("Тест просмотр всех фирм закончен!");
    }

    @Test
    @Order(1)
    @Override
    @DisplayName("Тест создание фирмы")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void createObject() throws Exception {
        log.info("Тест создание фирмы начат!");
        mvc.perform(MockMvcRequestBuilders.post("/company/add/{pattern}", "big")
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("companyForm", companyDTO)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("company"))
                .andExpect(redirectedUrlTemplate("/company"))
                .andExpect(redirectedUrl("/company"));
        log.info("Тест создание фирмы закончен!");
    }

    @Test
    @Order(2)
    @DisplayName("Тест редактирования фирмы")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void updateObject() throws Exception {
        log.info("Тест редактирования фирмы начат!");
        CompanyDTO company = companyService.getOne(companyDTO.getId());
        company.setTitle(companyDTOUpdate.getTitle());
        mvc.perform(post("/company/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .flashAttr("companyForm", companyDTOUpdate)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("company"))
                .andExpect(redirectedUrl("/company"));
        log.info("Тест редактирования фирмы начат!");
    }

    @Test
    @Order(3)
    @Override
    @DisplayName("Тест по софт удалению фирмы")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void deleteObject() throws Exception {
        log.info("Тест по софт удалению фирмы начат!");
        CompanyDTO company = companyService.getOne(companyDTO.getId());
        company.setDeleted(true);
        mvc.perform(get("/company/delete/{id}", company.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("id", company.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("company"))
                .andExpect(redirectedUrl("/company"));

        CompanyDTO deletedCompany = companyService.getOne(companyDTO.getId());
        assertTrue(deletedCompany.isDeleted());
        log.info("Тест по софт удалению фирмы закончен!");
    }

    @Test
    @Order(4)
    @DisplayName("Тест просмотр одной фирмы")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void getOneTask() throws Exception {
        log.info("Тест просмотр одной фирмы начат!");
        CompanyDTO company = companyService.getOne(companyDTO.getId());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/company/{id}", company.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("company", companyDTO)
                        .flashAttr("users1", 1L)
                        .flashAttr("users2", 2L)
                        .flashAttr("users", userService.listAll())
                        .flashAttr("companyId", company.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("company/view-one"))
                .andExpect(model().attributeExists("company", "users1", "users2", "users", "companyId"))
                .andReturn();
        log.info("Тест просмотр одной фирмы закончен!");
    }

    @Test
    @Order(5)
    @DisplayName("Тест по снятию мягкого удаления фирмы")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void restore() throws Exception {
        log.info("Тест по снятию мягкого удаления фирмы начат!");
        CompanyDTO company = companyService.getOne(companyDTO.getId());
        company.setDeleted(false);
        mvc.perform(get("/company/restore/{id}", company.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("id", company.getId())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("company"))
                .andExpect(redirectedUrl("/company"));
        CompanyDTO deletedCompany = companyService.getOne(companyDTO.getId());
        assertFalse(deletedCompany.isDeleted());
        log.info("Тест по снятию мягкого удаления фирмы закончен!");
    }
}
