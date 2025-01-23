package dev.lrxh.neptune.kit.command;


import com.jonahseguin.drink.annotation.Command;
import dev.lrxh.neptune.kit.menu.stats.StatsMenu;
import org.bukkit.entity.Player;

public class StatsCommand {

    @Command(name = "", desc = "")
    public void open(Player player) {
        new StatsMenu(player.getName()).open(player);

    }

    @Command(name = "", desc = "", usage = "<player>")
    public void statsOthers(Player player, Player target) {
        new StatsMenu(target).open(player);
    }
}