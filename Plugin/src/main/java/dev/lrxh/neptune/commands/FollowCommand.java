package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


@CommandAlias("follow")
@CommandPermission("neptune.admin.follow")
@Description("Follow Command for Neptune Practice Core.")
public class FollowCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Default
    @Syntax("<player>")
    @CommandCompletion("@names")
    public void follow(Player player, String name) {
        Player followingPlayer = Bukkit.getPlayer(name);
        if(followingPlayer == null){
            MessagesLocale.NOT_ONLINE.send(player.getUniqueId(), new Replacement("<player>", name));
            return;
        }
        SettingData followingSettingData = plugin.getProfileManager().getByUUID(followingPlayer.getUniqueId()).getSettingData();

        if(followingSettingData.getFollowings().contains(player.getUniqueId())) {
            followingSettingData.removeFollower(player.getUniqueId());
            MessagesLocale.STOP_FOLLOWING.send(player.getUniqueId(), new Replacement("<player>", name));
            return;
        }

        plugin.getProfileManager().getByUUID(followingPlayer.getUniqueId()).getSettingData().addFollower(player.getUniqueId());
        MessagesLocale.START_FOLLOW.send(player.getUniqueId(), new Replacement("<player>", name));
    }
}