package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.exception.CommandArgumentException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlagExtractor {

    private final DrinkCommandService commandService;

    public FlagExtractor(DrinkCommandService commandService) {
        this.commandService = commandService;
    }

    public Map<Character, CommandFlag> extractFlags(final @Nonnull List<String> args) throws CommandArgumentException {
        Preconditions.checkNotNull(args, "Args cannot be null");
        Map<Character, CommandFlag> flags = new HashMap<>();
        Iterator<String> it = args.iterator();
        Character currentFlag = null;
        while (it.hasNext()) {
            String arg = it.next();
            if (currentFlag != null) {
                if (!isFlag(arg)) {
                    // Value flag
                    flags.put(currentFlag, new CommandFlag(currentFlag, arg));
                } else {
                    // Boolean flag
                    flags.put(currentFlag, new CommandFlag(currentFlag, "true"));
                }
                it.remove();
                currentFlag = null;
            } else {
                if (isFlag(arg)) {
                    char f = getFlag(arg);
                    if (!flags.containsKey(f)) {
                        currentFlag = f;
                        if (!it.hasNext()) {
                            // Boolean flag
                            flags.put(currentFlag, new CommandFlag(currentFlag, "true"));
                            currentFlag = null;
                        }
                    } else {
                        throw new CommandArgumentException("The flag '-" + f + "' has already been provided in this command.");
                    }
                    it.remove();
                }
            }
        }
        return flags;
    }

    public char getFlag(@Nonnull String arg) {
        return arg.charAt(1);
    }

    public boolean isFlag(@Nonnull String arg) {
        return arg.length() == 2 && arg.charAt(0) == CommandFlag.FLAG_PREFIX;
    }

}
