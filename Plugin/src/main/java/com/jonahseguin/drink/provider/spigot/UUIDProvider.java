package com.jonahseguin.drink.provider.spigot;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDProvider extends DrinkProvider<UUID> {

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
    public UUID defaultNullValue() {
        return null;
    }

    @Nullable
    @Override
    public UUID provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        UUID uuid;
        try {
            uuid = UUID.fromString(name);
        } catch (IllegalArgumentException e) {
            throw new CommandExitMessage("Invalid UUID format for name '" + name + "'.");
        }

        return uuid;
    }

    @Override
    public String argumentDescription() {
        return "uuid";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return new ArrayList<>();
    }
}