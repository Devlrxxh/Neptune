package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateProvider extends DrinkProvider<Date> {

    public static final DateProvider INSTANCE = new DateProvider();

    public static final String FORMAT_STR = "yyyy-MM-dd@HH:mm";
    public static final DateFormat FORMAT = new SimpleDateFormat(FORMAT_STR, Locale.ENGLISH);

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public Date provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = arg.get();
        try {
            return FORMAT.parse(s);
        } catch (ParseException e) {
            throw new CommandExitMessage("Date must be in format: " + FORMAT_STR);
        }
    }

    @Override
    public String argumentDescription() {
        return "date: " + FORMAT_STR;
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        Calendar calendar = Calendar.getInstance();
        return Collections.singletonList(String.format("%d-%02d-%02d@%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
    }
}
