package com.sber.finalsberproject.controllers;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@Rollback(value = false)
public class MVCLoginControllerTest extends CommonTestMVC {
    @Test
    @Order(0)
    @DisplayName("Тест перехода на страницу логина")
    protected void login() throws Exception {
        log.info("Тест перехода на страницу логина начат!");
        mvc.perform(MockMvcRequestBuilders.get("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("login"));
        log.info("Тест перехода на страницу логина закончен!");
    }

    @Test
    @Order(1)
    @DisplayName("Тест входа в систему")
    protected void loginAccept() throws Exception {
        log.info("Тест входа в систему начат!");
        mvc.perform(formLogin().user("user1").password("user1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/other"));
        log.info("Тест входа в систему закончен!");
    }

    @Override
    protected void createObject() throws Exception {
    }

    @Override
    protected void updateObject() throws Exception {
    }

    @Override
    protected void deleteObject() throws Exception {
    }

    @Override
    protected void listAll() throws Exception {
    }
}
