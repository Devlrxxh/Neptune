package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {
    public String getDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm"));
    }
}
