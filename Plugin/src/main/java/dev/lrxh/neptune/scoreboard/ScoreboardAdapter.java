package dev.lrxh.neptune.scoreboard;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
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
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.LOBBY.getStringList()), player);
            case IN_KIT_EDITOR:
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.KIT_EDITOR.getStringList()), player);
            case IN_PARTY:
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.PARTY_LOBBY.getStringList()), player);
            case IN_QUEUE:
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_QUEUE.getStringList()), player);
            case IN_GAME:
                match = profile.getMatch();
                return match.getScoreboard(player.getUniqueId());
            case IN_SPECTATOR:
                match = profile.getMatch();
                if (match instanceof SoloFightMatch) {
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR_BEDWARS.getStringList()), player);
                    }
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR.getStringList()), player);
                } else if (match instanceof TeamFightMatch) {
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR_BEDWARS.getStringList()), player);
                    }
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR_TEAM.getStringList()), player);
                } else if (match instanceof FfaFightMatch) {
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR_FFA.getStringList()), player);
                }
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR.getStringList()), player);
            case IN_CUSTOM:
                return PlaceholderUtil.format(ScoreboardService.get().getScoreboardLines(profile.getCustomState(), profile), player);
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