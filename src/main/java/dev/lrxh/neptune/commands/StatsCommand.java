package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.providers.menus.stats.StatsMenu;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("stats")
@Description("Display player stats.")
public class StatsCommand extends BaseCommand {

    @Default
    public void open(Player player) {
        new StatsMenu(player.getName()).openMenu(player.getUniqueId());

    }

    @Default
    @Syntax("<name>")
    @CommandCompletion("@names")
    public void statsOthers(Player player, String otherPlayer) {
        if (Bukkit.getPlayer(otherPlayer) == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }
        new StatsMenu(otherPlayer).openMenu(player.getUniqueId());
    }
}