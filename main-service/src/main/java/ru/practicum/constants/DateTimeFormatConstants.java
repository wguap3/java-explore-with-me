package ru.practicum.constants;


import java.time.format.DateTimeFormatter;

public final class DateTimeFormatConstants {

    private DateTimeFormatConstants() {
    }

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
