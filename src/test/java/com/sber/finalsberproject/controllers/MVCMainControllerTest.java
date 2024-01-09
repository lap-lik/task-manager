package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.TaskDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static com.sber.finalsberproject.model.Pattern.BIG_TASKS;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@Rollback(value = false)
public class MVCMainControllerTest extends CommonTestMVC {
    @Autowired
    private MVCMainController mvcMainController;
    private final TaskDTO taskDTO = new TaskDTO("big_task_test1", null, null,
            null, null, 2L, false,
            BIG_TASKS, null, new ArrayList<>(1));

    @Test
    @Order(0)
    @Override
    @DisplayName("Тест просмотр всех задач")
    @WithAnonymousUser
    protected void listAll() throws Exception {
        log.info("Тест просмотр всех задач начат!");
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/other")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/main"))
                .andExpect(model().attributeExists("usersCompany", "bigTasks"))
                .andExpect(redirectedUrlTemplate("/index"))
                .andExpect(redirectedUrl("/index"))
                .andReturn();
        log.info("Тест просмотр всех задач закончен!");
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
}
