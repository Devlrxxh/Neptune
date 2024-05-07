package dev.lrxh.neptune.providers.scoreboard;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.PlaceholderUtil;
import dev.lrxh.neptune.utils.assemble.AssembleAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {
    private final Neptune plugin = Neptune.get();

    public String getTitle(Player player) {
        return getAnimatedText();
    }

    public List<String> getLines(Player player) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return new ArrayList<>();
        ProfileState state = profile.getState();
        if (state.equals(ProfileState.LOBBY) || state.equals(ProfileState.IN_KIT_EDITOR)) {
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.LOBBY.getStringList()), player);
        }
        if (state.equals(ProfileState.IN_QUEUE)) {
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_QUEUE.getStringList()), player);
        }
        if (state.equals(ProfileState.IN_GAME)) {
            Match match = profile.getMatch();
            if (match.getMatchState().equals(MatchState.STARTING)) {
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_STARTING.getStringList()), player);
            } else if (match.getMatchState().equals(MatchState.IN_ROUND)) {
                if (match.getRounds() > 1) {
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BEST_OF.getStringList()), player);
                }
                if (match.getKit().isBoxing()) {
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BOXING.getStringList()), player);
                }
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME.getStringList()), player);
            } else {
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_ENDED.getStringList()), player);
            }
        }
        if (state.equals(ProfileState.IN_SPECTATOR)) {
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_SPECTATOR.getStringList()), player);
        }
        return null;
    }

    private String getAnimatedText() {
        int index = (int) ((System.currentTimeMillis() / ScoreboardLocale.UPDATE_INTERVAL.getInt())
                % ScoreboardLocale.TITLE.getStringList().size());
        return ScoreboardLocale.TITLE.getStringList().get(index);
    }
}
