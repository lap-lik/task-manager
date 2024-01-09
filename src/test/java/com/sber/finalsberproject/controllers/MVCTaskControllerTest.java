package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.TaskDTO;
import com.sber.finalsberproject.service.TaskService;
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

import java.util.ArrayList;

import static com.sber.finalsberproject.model.Pattern.BIG_TASKS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@Rollback(value = false)
public class MVCTaskControllerTest extends CommonTestMVC {
    @Autowired
    private TaskService taskService;
    private final TaskDTO taskDTO = new TaskDTO("big_task_test1", null, null,
            null, null, 2L, false,
            BIG_TASKS, null, new ArrayList<>(1));
    private final TaskDTO taskDTOUpdate = new TaskDTO("big_task_test1-update", null,
            null, null, null, 2L, false,
            BIG_TASKS, null, new ArrayList<>(1));

    @Test
    @Order(0)
    @Override
    @DisplayName("Тест просмотр всех задач")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void listAll() throws Exception {
        log.info("Тест просмотр всех задач начат!");
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/tasks/{pattern}/view-all/{condition}", "big", "open")
                        .param("page", "1")
                        .param("size", "25")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("tasks/big/view-all"))
                .andExpect(model().attributeExists("usersCompany", "bigTasks"))
                .andReturn();
        log.info("Тест просмотр всех задач закончен!");
    }

    @Test
    @Order(1)
    @Override
    @DisplayName("Тест создание задачи")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void createObject() throws Exception {
        log.info("Тест создание задачи начат!");
        mvc.perform(MockMvcRequestBuilders.post("/add/{pattern}", "big")
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("authorForm", taskDTO)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks/big/view-all/open"))
                .andExpect(redirectedUrlTemplate("/tasks/big/view-all/open"))
                .andExpect(redirectedUrl("/tasks/big/view-all/open"));
        log.info("Тест создание задачи закончен!");
    }

    @Test
    @Order(2)
    @Override
    @DisplayName("Тест редактирования задачи")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void updateObject() throws Exception {
        log.info("Тест редактирования задачи начат!");
        TaskDTO task = taskService.getOne(taskDTO.getId());
        task.setTaskTitle(taskDTOUpdate.getTaskTitle());
        mvc.perform(post("/tasks/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .flashAttr("taskForm", task)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/other"))
                .andExpect(redirectedUrl("/other"));
        log.info("Тест редактирования задачи начат!");
    }

    @Test
    @Order(3)
    @Override
    @DisplayName("Тест по софт удалению задачи")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void deleteObject() throws Exception {
        TaskDTO task = taskService.getOne(taskDTOUpdate.getId());
        task.setDeleted(true);
        mvc.perform(get("/tasks/delete/{pattern}/{id}", "big", task.getId())
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks/big/view-all"))
                .andExpect(redirectedUrl("/tasks/big/view-all"));

        TaskDTO deletedTask = taskService.getOne(taskDTOUpdate.getId());
        assertTrue(deletedTask.isDeleted());
        log.info("Тест по софт удалению задачи закончен!");
    }

    @Test
    @Order(4)
    @DisplayName("Тест просмотр одной задачи")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void getOneTask() throws Exception {
        log.info("Тест просмотр одной задачи начат!");
        TaskDTO task = taskService.getOne(taskDTO.getId());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/tasks/{pattern}/{taskId}", "big", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("authorForm", taskDTO)
                        .flashAttr("id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("tasks/big/view-one"))
                .andExpect(model().attributeExists("task", "userDTO", "usersDTO", "usersByTaskId", "taskComments", "taskByTasks"))
                .andReturn();
        log.info("Тест просмотр одной задачи закончен!");
    }

    @Test
    @Order(5)
    @DisplayName("Тест на закрытие задачи")
    @WithAnonymousUser
    protected void closed() throws Exception {
        log.info("Тест на закрытие задачи начат!");
        TaskDTO task = taskService.getOne(taskDTOUpdate.getId());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/tasks/closed/{pattern}/{taskId}", "big", task.getId())
                        .param("userId", "1")
                        .param("taskId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("tasks/big/view-all/open"))
                .andReturn();
        log.info("Тест на закрытие задачи закончен!");
    }

    @Test
    @Order(6)
    @DisplayName("Тест на возобнавление задачи")
    @WithAnonymousUser
    protected void disclosure() throws Exception {
        log.info("Тест на возобнавление задачи начат!");
        TaskDTO task = taskService.getOne(taskDTOUpdate.getId());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/tasks/disclosure/{pattern}/{taskId}", "big", task.getId())
                        .param("taskId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("tasks/big/view-all/open"))
                .andReturn();
        log.info("Тест на возобнавление задачи закончен!");
    }

    @Test
    @Order(7)
    @DisplayName("Тест на добавление ответсвенного по задаче")
    @WithAnonymousUser
    protected void addResponsible() throws Exception {
        log.info("Тест на добавление ответсвенного по задаче начат!");
        TaskDTO task = taskService.getOne(taskDTOUpdate.getId());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/tasks/responsible/add/{pattern}/{taskId}/{userId}", "big", task.getId(), 1)
                        .param("taskId", "1")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("tasks/big/"))
                .andReturn();
        log.info("Тест на добавление ответсвенного по задаче закончен!");
    }

    @Test
    @Order(8)
    @DisplayName("Тест на снятие ответсвенного по задаче")
    @WithAnonymousUser
    protected void removeResponsible() throws Exception {
        log.info("Тест на снятие ответсвенного по задаче начат!");
        TaskDTO task = taskService.getOne(taskDTOUpdate.getId());
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/tasks/responsible/remove/{pattern}/{taskId}", "big", task.getId())
                        .param("taskId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("tasks/big/"))
                .andReturn();
        log.info("Тест на снятие ответсвенного по задаче закончен!");
    }

    @Test
    @Order(9)
    @DisplayName("Тест по снятию мягкого удаления задачи")
    @WithMockUser(username = "admin", roles = "ADMIN", password = "admin")
    protected void restore() throws Exception {
        TaskDTO task = taskService.getOne(taskDTOUpdate.getId());
        task.setDeleted(false);
        mvc.perform(get("/tasks/restore/{id}", task.getId())
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks"))
                .andExpect(redirectedUrl("/tasks"));

        TaskDTO deletedTask = taskService.getOne(taskDTOUpdate.getId());
        assertFalse(deletedTask.isDeleted());
        log.info("Тест по софт удалению задачи закончен!");
    }
}
