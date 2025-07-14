package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.exception.CommandRegistrationException;
import com.jonahseguin.drink.exception.CommandStructureException;
import com.jonahseguin.drink.exception.MissingProviderException;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandExtractor {

    private final DrinkCommandService commandService;

    public CommandExtractor(DrinkCommandService commandService) {
        this.commandService = commandService;
    }

    public Map<String, DrinkCommand> extractCommands(@Nonnull Object handler) throws MissingProviderException, CommandStructureException {
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        final Map<String, DrinkCommand> commands = new HashMap<>();
        for (Method method : handler.getClass().getDeclaredMethods()) {
            Optional<DrinkCommand> o = extractCommand(handler, method);
            if (o.isPresent()) {
                DrinkCommand drinkCommand = o.get();
                commands.put(commandService.getCommandKey(drinkCommand.getName()), drinkCommand);
            }
        }
        return commands;
    }

    private Optional<DrinkCommand> extractCommand(@Nonnull Object handler, @Nonnull Method method) throws MissingProviderException, CommandStructureException {
        Preconditions.checkNotNull(handler, "Handler object cannot be null");
        Preconditions.checkNotNull(method, "Method cannot be null");
        if (method.isAnnotationPresent(Command.class)) {
            try {
                method.setAccessible(true);
            } catch (SecurityException ex) {
                throw new CommandRegistrationException("Couldn't access method " + method.getName());
            }
            Command command = method.getAnnotation(Command.class);
            String perm = "";
            if (method.isAnnotationPresent(Require.class)) {
                Require require = method.getAnnotation(Require.class);
                perm = require.value();
            } else if (method.getDeclaringClass().isAnnotationPresent(Require.class)) {
                Require require = method.getDeclaringClass().getAnnotation(Require.class);
                perm = require.value() + (command.name().isEmpty() ? "" : ".") + command.name();
            }
            DrinkCommand drinkCommand = new DrinkCommand(
                    commandService, command.name(), Sets.newHashSet(command.aliases()), command.desc(), command.usage(), command.async(),
                    perm, command.hidden(), handler, method
            );
            return Optional.of(drinkCommand);
        }
        return Optional.empty();
    }

}
