package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lrxh.neptune.match.menu.MatchHistoryMenu;
import org.bukkit.entity.Player;

@CommandAlias("matchhistory")
@Description("Open match history.")
public class MatchHistoryCommand extends BaseCommand {
    @Default
    public void open(Player player) {
        new MatchHistoryMenu().openMenu(player.getUniqueId());
    }
}
