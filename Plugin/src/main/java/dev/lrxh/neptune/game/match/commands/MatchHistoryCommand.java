package dev.lrxh.neptune.game.match.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.game.match.menu.MatchHistoryMenu;
import org.bukkit.entity.Player;

public class MatchHistoryCommand {
    @Command(name = "", desc = "Open match history")
    public void open(@Sender Player player) {
        new MatchHistoryMenu().open(player);
    }
}
