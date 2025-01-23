package dev.lrxh.neptune.leaderboard.command;

import com.jonahseguin.drink.annotation.Command;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.menu.LeaderboardMenu;
import org.bukkit.entity.Player;

public class LeaderboardCommand {

    @Command(name = "", desc = "")
    public void open(Player player) {
        new LeaderboardMenu(LeaderboardType.WINS).open(player);
    }

    @Command(name = "wins", desc = "")
    public void wins(Player player) {
        new LeaderboardMenu(LeaderboardType.WINS).open(player);
    }

    @Command(name = "winStreak", desc = "")
    public void winStreak(Player player) {
        new LeaderboardMenu(LeaderboardType.BEST_WIN_STREAK).open(player);
    }

    @Command(name = "losses", desc = "")
    public void losses(Player player) {
        new LeaderboardMenu(LeaderboardType.DEATHS).open(player);
    }
}
