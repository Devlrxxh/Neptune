package com.jonahseguin.drink.parametric;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public abstract class DrinkProvider<T> {

    public abstract boolean doesConsumeArgument();

    public abstract boolean isAsync();

    public boolean allowNullArgument() {
        return true;
    }

    @Nullable
    public T defaultNullValue() {
        return null;
    }

    @Nullable
    public abstract T provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage;

    public abstract String argumentDescription();

    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }

    public List<String> getSuggestions(CommandSender sender, @Nonnull String prefix) {
        return getSuggestions(prefix);
    }

    protected boolean hasAnnotation(List<? extends Annotation> list, Class<? extends Annotation> a) {
        return list.stream().anyMatch(annotation -> annotation.annotationType().equals(a));
    }

}
