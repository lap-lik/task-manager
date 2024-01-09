package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.*;
import com.sber.finalsberproject.exception.MyDeleteException;
import com.sber.finalsberproject.mapper.TaskMapper;
import com.sber.finalsberproject.repository.TaskRepository;
import com.sber.finalsberproject.service.CommentService;
import com.sber.finalsberproject.service.TaskService;
import com.sber.finalsberproject.service.UserService;
import com.sber.finalsberproject.service.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/tasks")
public class MVCTaskController {
    private final TaskService taskService;
    private final CommentService commentService;

    private final UserService userService;


    public MVCTaskController(TaskService taskService,
                             CommentService commentService,
                             UserService userService) {
        this.taskService = taskService;
        this.commentService = commentService;
        this.userService = userService;
    }

    private CustomUserDetails myCustomUserDetails() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    @PostMapping("/{pattern}/add_user")
    public String addUserToTask(@RequestParam Long taskId, @RequestParam Long userId, @PathVariable String pattern) {
        taskService.addTaskToUser(taskId, userId);
        if (pattern.equals("big")){
            return "redirect:/tasks/big/" + taskId;
        } else {
            return "redirect:/tasks/medium/" + taskId;
        }
    }

    @GetMapping("/remove-user/{pattern}/{taskId}")
    public String removeUserFromTask(@PathVariable String pattern, @PathVariable Long taskId) {
        taskService.removeUserFromTask(taskId, Long.valueOf(myCustomUserDetails().getUserId()));
        return switch (pattern) {
            case "big" -> "redirect:/tasks/big/view-all/open";
            case "medium" -> "redirect:/tasks/medium/view-all/open";
            default -> "redirect:/tasks/small/view-all/open";
        };
    }


    @GetMapping("/{pattern}/{id}")
    public String getOneTask(@PathVariable Long id,
                             @PathVariable String pattern,
                             @ModelAttribute(name = "exception") final String exception,
                             Model model) {

        model.addAttribute("task", taskService.getOne(id));
        UserDTO userDTO = userService.getOne(Long.valueOf(myCustomUserDetails().getUserId()));
        model.addAttribute("userDTO", userDTO);
        List<UserDTO> usersDTO = userService.listAllByCompany(userDTO.getCompanyId());
        model.addAttribute("usersDTO", usersDTO);
        List<UserDTO> usersByTaskId = userService.listAllByTaskId(id);
        model.addAttribute("usersByTaskId", usersByTaskId);
        List<CommentDTO> taskComments = commentService.getAllCommentsByTaskId(id);
        model.addAttribute("taskComments", taskComments);
        List<TaskDTO> taskByTasks = taskService.getAllTasksByTaskId(id);
        model.addAttribute("taskByTasks", taskByTasks);
        switch (pattern) {
            case "big": return "/tasks/big/view-one";
            case "medium": return "/tasks/medium/view-one";
            case "medium-at-big": return "/tasks/big/view-one-closed";
            default : return "/tasks/medium/view-one-closed";
        }
    }
    @GetMapping("/{pattern}/view-all/{condition}")
    public String getAllTasks(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "25") int pageSize,
                              @ModelAttribute(name = "exception") final String exception,
                              @PathVariable String pattern, @PathVariable String condition,
                              Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "created_when"));
        String userName = myCustomUserDetails().getAuthorities().toString();
        Page<TaskDTO> result;
        model.addAttribute("usersCompany", userService.listAllByCompany(userService.getOne(Long.valueOf(myCustomUserDetails().getUserId())).getCompanyId()));
        if ("[ROLE_ADMINISTRATOR]".equalsIgnoreCase(userName)) {
            result = taskService.listAllByPattern(pageRequest, pattern);
        } else {
            result = taskService.getAllByUserId(Long.valueOf(myCustomUserDetails().getUserId()), pageRequest, pattern);
        }
        switch (pattern) {
            case "big" -> {
                model.addAttribute("bigTasks", result);
                if (condition.equals("open")) {
                    return "/tasks/big/view-all";
                } else {
                    return "/tasks/big/view-all-closed";
                }
            }
            case "medium" -> {
                model.addAttribute("mediumTasks", result);
                if (condition.equals("open")) {
                    return "/tasks/medium/view-all";
                } else {
                    return "/tasks/medium/view-all-closed";
                }

            }
            default -> {
                model.addAttribute("smallTasks", result);
                if (condition.equals("open")) {
                    return "/tasks/small/view-all";
                } else {
                    return "/tasks/small/view-all-closed";
                }
            }
        }
    }

        @GetMapping("/add/{pattern}")
        public String create (@PathVariable String pattern){
            return "index";
        }

        @PostMapping("/add/{pattern}")
        public String create (@ModelAttribute("mediumTaskForm") TaskDTO newTask,
                @PathVariable String pattern){
            List<Long> userId = new ArrayList<>();
            userId.add(Long.valueOf(myCustomUserDetails().getUserId()));
            newTask.setUsersIds(userId);
            taskService.create(newTask);
            return switch (pattern) {
                case "big" -> "redirect:/tasks/big/view-all/open";
                case "medium" -> "redirect:/tasks/medium/view-all/open";
                case "small" -> "redirect:/tasks/small/view-all/open";
                case "of-big" -> "redirect:/tasks/big/" + newTask.getTaskId();
                case "of-medium" -> "redirect:/tasks/medium/" + newTask.getTaskId();
                default -> "redirect:/other";
            };
        }

        @GetMapping("/update/{id}")
        public String update (@PathVariable Long id,
                Model model){
            model.addAttribute("task", taskService.getOne(id));
            return "/tasks/update";
        }
        @PostMapping("/update")
        public String update (@ModelAttribute("taskForm") TaskDTO taskDTO){
            TaskDTO task = taskService.getOne(taskDTO.getId());
            log.info("Пришло: " + taskDTO.getTaskTitle() + " and " + taskDTO.getFixPeriod());
            task.setTaskTitle(taskDTO.getTaskTitle());
            task.setFixPeriod(taskDTO.getFixPeriod());
            taskService.update(task);
            return "redirect:/other";
        }

        @GetMapping("/closed/{pattern}/{taskId}")
        public String closed (@PathVariable Long taskId, @PathVariable String pattern) throws MyDeleteException {
            taskService.closed(taskId, Long.valueOf(myCustomUserDetails().getUserId()));
            return switch (pattern) {
                case "big" -> "redirect:/tasks/big/view-all/open";
                case "medium" -> "redirect:/tasks/medium/view-all/open";
                case "other" -> "redirect:/other";
                default -> "redirect:/tasks/small/view-all/open";
            };
        }

        @GetMapping("/disclosure/{pattern}/{taskId}")
        public String disclosure (@PathVariable Long taskId, @PathVariable String pattern){
            taskService.disclosure(taskId, Long.valueOf(myCustomUserDetails().getUserId()));
            return switch (pattern) {
                case "big" -> "redirect:/tasks/big/view-all/open";
                case "medium" -> "redirect:/tasks/medium/view-all/open";
                default -> "redirect:/tasks/small/view-all/open";
            };
        }
        @GetMapping("/responsible/add/{pattern}/{taskId}/{userId}")
        public String addResponsible (@PathVariable String pattern, @PathVariable Long taskId, @PathVariable Long userId)
        {
            taskService.addResponsible(taskId, userId);
            if (pattern.equals("big")) {
                return "redirect:/tasks/big/" + taskId;
            } else {
                return "redirect:/tasks/medium/" + taskId;
            }
        }
        @GetMapping("/responsible/remove/{pattern}/{taskId}")
        public String removeResponsible (@PathVariable String pattern, @PathVariable Long taskId){
            taskService.removeResponsible(taskId);
            if (pattern.equals("big")) {
                return "redirect:/tasks/big/" + taskId;
            } else {
                return "redirect:/tasks/medium/" + taskId;
            }
        }

        @GetMapping("/delete/{pattern}/{id}")
        public String delete (@PathVariable Long id, @PathVariable String pattern) throws MyDeleteException {
            taskService.deleteSoft(id);
            return switch (pattern) {
                case "big" -> "redirect:/tasks/big/view-all";
                case "medium" -> "redirect:/tasks/medium/view-all";
                default -> "redirect:/tasks/small/view-all";
            };
        }

        @GetMapping("/restore/{id}")
        public String restore (@PathVariable Long id){
            taskService.restore(id);
            return "redirect:/tasks";
        }

        @PostMapping("/search")
        public String searchTasks ( @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "5") int pageSize,
        @ModelAttribute("taskSearchForm") SearchDTO searchDTO,
        Model model){
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "taskTitle"));
            model.addAttribute("tasks", taskService.searchTask(searchDTO, pageRequest));
            return "medium-task-view-all";
        }
    }
