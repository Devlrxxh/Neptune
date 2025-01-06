package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.data.SettingData;
import org.bukkit.entity.Player;

@CommandAlias("unfollow")
@CommandPermission("neptune.admin.follow")
@Description("Unfollow Command for Neptune Practice Core.")
public class UnfollowCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Default
    public void unfollow(Player player) {
        SettingData playerSettings = plugin.getAPI().getProfile(player.getUniqueId()).getSettingData();
        
        if (playerSettings.getFollowings().isEmpty()) {
            MessagesLocale.NOT_FOLLOWING.send(player.getUniqueId());
            return;
        }

        playerSettings.getFollowings().clear();
        MessagesLocale.STOPPED_FOLLOWING_ALL.send(player.getUniqueId());
    }
}
