package dev.lrxh.neptune.game.ffa.command;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.ffa.FFAArena;
import dev.lrxh.neptune.game.ffa.FFAService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class FFAArenaProvider extends DrinkProvider<FFAArena> {

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
    public FFAArena defaultNullValue() {
        return null;
    }

    @Nullable
    @Override
    public FFAArena provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        FFAArena arena = FFAService.get().getArenaByName(name);
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

        for (FFAArena arena : FFAService.get().getArenas()) {
            arenas.add(arena.getName());
        }

        return arenas;
    }
}
