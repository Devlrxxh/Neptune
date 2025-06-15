package dev.lrxh.neptune.game.arena.command;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class StandaloneArenaProvider extends DrinkProvider<StandAloneArena> {

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
    public StandAloneArena defaultNullValue() {
        return null;
    }

    @Nullable
    @Override
    public StandAloneArena provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Arena arena = ArenaService.get().getArenaByName(name);
        if (arena == null) throw new CommandExitMessage("Arena doesn't exist");
        if (!(arena instanceof StandAloneArena standAloneArena)) throw new CommandExitMessage("Arena isn't standalone");

        return standAloneArena;
    }

    @Override
    public String argumentDescription() {
        return "standalone arena";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        List<String> arenas = new ArrayList<>();
        for (Arena arena : ArenaService.get().getArenas()) {
            if (arena instanceof StandAloneArena standAloneArena)
                if (!standAloneArena.isCopy()) arenas.add(arena.getName());
        }

        return arenas;
    }
}
