package dev.lrxh.neptune.leaderboard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.menu.LeaderboardMenu;
import org.bukkit.entity.Player;

@CommandAlias("leaderboard|leaderboards|lb|lbs")
@Description("Open leaderboards.")
public class LeaderboardCommand extends BaseCommand {
    @Default
    public void open(Player player) {
        new LeaderboardMenu(LeaderboardType.WINS).openMenu(player);
    }

    @Subcommand("wins")
    public void wins(Player player) {
        new LeaderboardMenu(LeaderboardType.WINS).openMenu(player);
    }

    @Subcommand("winStreak")
    public void winStreak(Player player) {
        new LeaderboardMenu(LeaderboardType.BEST_WIN_STREAK).openMenu(player);
    }

    @Subcommand("losses")
    public void losses(Player player) {
        new LeaderboardMenu(LeaderboardType.DEATHS).openMenu(player);
    }
}
