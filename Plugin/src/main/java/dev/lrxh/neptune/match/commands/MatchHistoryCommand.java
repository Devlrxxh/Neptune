package dev.lrxh.neptune.match.commands;

import com.jonahseguin.drink.annotation.Command;
import dev.lrxh.neptune.match.menu.MatchHistoryMenu;
import org.bukkit.entity.Player;

public class MatchHistoryCommand {
    @Command(name = "", desc = "Open match history")
    public void open(Player player) {
        new MatchHistoryMenu().open(player);
    }
}
