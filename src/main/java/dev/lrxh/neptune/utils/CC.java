package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CC {


    public String error(String message) {
        return color("&4Error &8- &c" + message);
    }

    public String color(String text) {
        return Neptune.get().getVersionHandler().getColorUtils().color(text);
    }
}
