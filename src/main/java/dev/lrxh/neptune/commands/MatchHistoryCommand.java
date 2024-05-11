package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.menu.MatchHistoryMenu;
import dev.lrxh.neptune.profile.Profile;
import org.bukkit.entity.Player;

@CommandAlias("matchhistory")
@Description("Open match history.")
public class MatchHistoryCommand extends BaseCommand {
    @Default
    public void open(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());

        new MatchHistoryMenu().openMenu(player);
    }
}
