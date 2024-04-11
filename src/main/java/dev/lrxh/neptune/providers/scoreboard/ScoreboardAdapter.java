package dev.lrxh.neptune.providers.scoreboard;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.PlaceholderUtil;
import dev.lrxh.neptune.utils.assemble.AssembleAdapter;
import org.bukkit.configuration.file.YamlConfiguration;
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
        ProfileState state = profile.getState();
        YamlConfiguration config = plugin.getConfigManager().getScoreboardConfig().getConfiguration();

        if (state.equals(ProfileState.LOBBY)) {
            return PlaceholderUtil.format(new ArrayList<>(config.getStringList("SCOREBOARDS.LOBBY")), player);
        }
        if (state.equals(ProfileState.IN_QUEUE)) {
            return PlaceholderUtil.format(new ArrayList<>(config.getStringList("SCOREBOARDS.IN_QUEUE")), player);
        }
        return null;
    }

    private String getAnimatedText() {
        YamlConfiguration config = Neptune.get().getConfigManager().getScoreboardConfig().getConfiguration();

        int index = (int) ((System.currentTimeMillis() / config.getInt("SCOREBOARDS.UPDATE-INTERVAL"))
                % config.getStringList("SCOREBOARDS.TITLE").size());
        return config.getStringList("SCOREBOARDS.TITLE").get(index);
    }
}
