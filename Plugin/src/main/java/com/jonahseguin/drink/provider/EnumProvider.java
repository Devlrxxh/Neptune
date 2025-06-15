package com.jonahseguin.drink.provider;

import com.google.common.collect.Lists;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.regex.Pattern;

public class EnumProvider<T extends Enum<T>> extends DrinkProvider<T> {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private final Class<T> enumClass;

    public EnumProvider(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    private static String simplify(String t) {
        return NON_ALPHANUMERIC.matcher(t.toLowerCase()).replaceAll("");
    }

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public T provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        String s = simplify(name);

        for (T entry : enumClass.getEnumConstants()) {
            if (simplify(entry.name()).equalsIgnoreCase(s)) {
                return entry;
            }
        }
        throw new CommandExitMessage("No matching value found for " + argumentDescription() + ".  Available values: " + StringUtils.join(getSuggestions(arg.getSender(), ""), ' '));
    }

    @Override
    public String argumentDescription() {
        return enumClass.getSimpleName();
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        List<String> suggestions = Lists.newArrayList();
        String test = simplify(prefix);

        for (T entry : enumClass.getEnumConstants()) {
            String name = simplify(entry.name());
            if (test.length() == 0 || name.startsWith(test)) {
                suggestions.add(entry.name().toLowerCase());
            }
        }

        return suggestions;
    }
}
