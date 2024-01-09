package com.sber.finalsberproject.constants;

public interface Errors {
    class TASK {
        public static final String TASK_DELETE_ERROR = "Задача не может быть удалена, ...";
    }

    class Company {
        public static final String COMPANY_DELETE_ERROR = "Компания не может быть удалена, так как есть пользователи, удалите пользователей.";
    }

    class Users {
        public static final String USER_FORBIDDEN_ERROR = "У вас нет прав просматривать информацию о пользователе.";
    }
}
