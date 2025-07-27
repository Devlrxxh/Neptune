package dev.lrxh.neptune.game.arena.command;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ArenaProvider extends DrinkProvider<Arena> {

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
    public Arena defaultNullValue() {
        return null;
    }

    @Nullable
    @Override
    public Arena provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Arena arena = ArenaService.get().getArenaByName(name);
        if (arena == null) throw new CommandExitMessage("Arena doesn't exist");

        return arena;
    }

    @Override
    public String argumentDescription() {
        return "arena";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        List<String> arenas = new ArrayList<>();

        for (Arena arena : ArenaService.get().getArenas()) {
            arenas.add(arena.getName());
        }

        return arenas;
    }
}
