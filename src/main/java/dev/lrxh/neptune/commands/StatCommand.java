package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lrxh.neptune.providers.menus.stats.StatsMenu;
import org.bukkit.entity.Player;

@CommandAlias("stats")
@Description("Statistics")
public class StatCommand extends BaseCommand {

    @Default
    public void queue(Player player) {
        new StatsMenu().openMenu(player);
    }
}