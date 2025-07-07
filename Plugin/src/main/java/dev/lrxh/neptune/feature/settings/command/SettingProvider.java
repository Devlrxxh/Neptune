package dev.lrxh.neptune.feature.settings.command;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import dev.lrxh.neptune.feature.settings.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingProvider extends DrinkProvider<Setting> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Nullable
    @Override
    public Setting defaultNullValue() {
        return null;
    }

    @Nullable
    @Override
    public Setting provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Setting setting;
        try {
            setting = Setting.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new CommandExitMessage("Invalid setting name: " + name);
        }

        return setting;
    }

    @Override
    public String argumentDescription() {
        return "standalone arena";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Arrays.stream(Setting.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
