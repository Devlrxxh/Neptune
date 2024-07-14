package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CC {


    public String error(String message) {
        return color(MessagesLocale.ERROR_MESSAGE.getString().replace("<error>", message));
    }

    public String color(String text) {
        return Neptune.get().getVersionHandler().getColorUtils().color(text);
    }

    public List<Object> color(List<Object> input) {
        return ColorUtil.addColors(input);
    }
}
