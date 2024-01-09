package com.sber.finalsberproject.model;

public enum Pattern {
    BIG_TASKS("Проекты."),

    MEDIUM_TASKS("Задачи."),

    SMALL_TASKS("Напоминания.");

    private final String patternTextDisplay;

    Pattern(String text) {
        this.patternTextDisplay = text;
    }

    public String getPatternTextDisplay() {
        return patternTextDisplay;
    }
}
