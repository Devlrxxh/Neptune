package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CC {


    public String error(String message) {
        return color(MessagesLocale.ERROR_MESSAGE.getString().replace("<error>", message));
    }

    public String color(String text) {
        return Neptune.get().getVersionHandler().getColorUtils().color(text);
    }
}
