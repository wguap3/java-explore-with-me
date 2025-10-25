package ru.practicum.enums;

public enum SortMode {
    EVENT_DATE,
    VIEWS;

    public static SortMode fromString(String value) {
        if (value == null) return EVENT_DATE;
        try {
            return SortMode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EVENT_DATE;
        }
    }
}

