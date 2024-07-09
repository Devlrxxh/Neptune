package dev.lrxh.neptune.providers.scoreboard;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.PlaceholderUtil;
import dev.lrxh.neptune.utils.assemble.AssembleAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {
    private final Neptune plugin;

    public ScoreboardAdapter() {
        this.plugin = Neptune.get();
    }

    public String getTitle(Player player) {
        return getAnimatedText();
    }

    public List<String> getLines(Player player) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return new ArrayList<>();

        ProfileState state = profile.getState();
        Match match;

        switch (state) {
            case IN_LOBBY:
            case IN_KIT_EDITOR:
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.LOBBY.getStringList()), player);
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
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR.getStringList()), player);
                } else if (match instanceof TeamFightMatch) {
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR_TEAM.getStringList()), player);
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
