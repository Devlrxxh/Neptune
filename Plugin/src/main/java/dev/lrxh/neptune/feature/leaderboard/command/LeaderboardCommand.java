package dev.lrxh.neptune.feature.leaderboard.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.menu.LeaderboardMenu;
import org.bukkit.entity.Player;

public class LeaderboardCommand {

    @Command(name = "", desc = "")
    public void open(@Sender Player player) {
        new LeaderboardMenu(LeaderboardType.KILLS).open(player);
    }

    @Command(name = "kills", desc = "")
    public void kills(@Sender Player player) {
        new LeaderboardMenu(LeaderboardType.KILLS).open(player);
    }

    @Command(name = "bestWinStreak", desc = "")
    public void bestWinStreak(@Sender Player player) {
        new LeaderboardMenu(LeaderboardType.BEST_WIN_STREAK).open(player);
    }

    @Command(name = "deaths", desc = "")
    public void deaths(@Sender Player player) {
        new LeaderboardMenu(LeaderboardType.DEATHS).open(player);
    }


}
