package dev.lrxh.neptune.providers.scoreboard;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import fr.mrmicky.fastboard.FastAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements FastAdapter {
    public String getTitle(Player player) {
        return PlaceholderUtil.format(getAnimatedText(), player);
    }

    public List<String> getLines(Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null) return new ArrayList<>();

        ProfileState state = profile.getState();
        Match match;

        switch (state) {
            case IN_LOBBY:
            case IN_KIT_EDITOR:
                if (!SettingsLocale.ENABLED_SCOREBOARD_LOBBY.getBoolean()) return new ArrayList<>();
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.LOBBY.getStringList()), player);
            case IN_PARTY:
                if (!SettingsLocale.ENABLED_SCOREBOARD_PARTY.getBoolean()) return new ArrayList<>();
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.PARTY_LOBBY.getStringList()), player);
            case IN_QUEUE:
                if (!SettingsLocale.ENABLED_SCOREBOARD_QUEUE.getBoolean()) return new ArrayList<>();
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_QUEUE.getStringList()), player);
            case IN_GAME:
                if (!SettingsLocale.ENABLED_SCOREBOARD_INGAME.getBoolean()) return new ArrayList<>();
                match = profile.getMatch();
                return match.getScoreboard(player.getUniqueId());
            case IN_SPECTATOR:
                if (!SettingsLocale.ENABLED_SCOREBOARD_SPECTATOR.getBoolean()) return new ArrayList<>();
                match = profile.getMatch();
                if (match instanceof SoloFightMatch) {
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR.getStringList()), player);
                } else if (match instanceof TeamFightMatch) {
                    if (!SettingsLocale.ENABLED_SCOREBOARD_SPECTATOR_TEAM.getBoolean()) return new ArrayList<>();
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR_TEAM.getStringList()), player);
                } else if (match instanceof FfaFightMatch) {
                    if (!SettingsLocale.ENABLED_SCOREBOARD_SPECTATOR_FFA.getBoolean()) return new ArrayList<>();
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR_FFA.getStringList()), player);
                }
                break;
            default:
                break;
        }

        return new ArrayList<>();
    }

    private String getAnimatedText() {
        int index = (int) ((System.currentTimeMillis() / ScoreboardLocale.UPDATE_INTERVAL.getInt())
                % ScoreboardLocale.TITLE.getStringList().size());
        return ScoreboardLocale.TITLE.getStringList().get(index);
    }
}
