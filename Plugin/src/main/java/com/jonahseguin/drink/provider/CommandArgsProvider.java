package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.argument.CommandArgs;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class CommandArgsProvider extends DrinkProvider<CommandArgs> {

    public static final CommandArgsProvider INSTANCE = new CommandArgsProvider();

    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public CommandArgs provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return arg.getArgs();
    }

    @Override
    public String argumentDescription() {
        return "args";
    }

}
