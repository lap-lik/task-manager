package com.sber.finalsberproject.constants;

import java.util.List;

public interface SecurityConstants {
    List<String> RESOURCES_WHITE_LIST = List.of("/resources/**",
            "/static/**",
            "/js/**",
            "/css/**",
            "files/**",
            "files/files/**",
            "files/avatars/**",
            "/img/**",
            "/carousel/**",
            "/",
            "/error",
            "/swagger-ui/**",
            "/webjars/bootstrap/5.3.0/**",
            "/webjars/bootstrap/5.3.0/css/**",
            "/webjars/bootstrap/5.3.0/js/**",
            "/v3/api-docs/**");

    List<String> COMMENTS_WHITE_LIST = List.of("/comments",
            "/comments/search",
            "/comments/{id}");

    List<String> TASK_WHITE_LIST = List.of("/tasks",
            "/tasks/search",
            "/tasks/medium",
            "/tasks/view-all-big",
            "/tasks/big-add",
            "/tasks/small",
            "/tasks/search/subtasksByTasks",
            "/tasks/{id}");
    List<String> SUBTASK_WHITE_LIST = List.of("/subtasks",
            "/subtasks/search",
            "/subtasks/{id}");

    List<String> COMMENTS_PERMISSION_LIST = List.of("/comments/add",
            "/comments/update",
            "/comments/delete");

    List<String> TASK_PERMISSION_LIST = List.of("/tasks/add",
            "/tasks/medium",
            "/tasks/view-all-big",
            "/tasks/big-add",
            "/tasks/small",
            "/tasks/update",
            "/tasks/delete",
            "/tasks/download/{taskId}");

    List<String> SUBTASK_PERMISSION_LIST = List.of("/subtasks/add",
            "/subtasks/update",
            "/subtasks/delete");

    List<String> USERS_WHITE_LIST = List.of(
            "/login",
            "/users/registration",
            "files/**",
            "/users/profile",
            "/users/profile/**",
            "files/files/**",
            "files/avatars/**",
            "/users/remember-password",
            "/users/change-password");

    List<String> USERS_PERMISSION_LIST = List.of("/add/contacts/*");
}
