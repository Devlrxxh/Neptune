package dev.lrxh.neptune.match.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("spectate|spec")
@Description("Spectate a match.")
public class SpectateCommand extends BaseCommand {


    @Default
    @Syntax("<name>")
    @CommandCompletion("@names")
    public void spectate(Player player, String otherPlayer) {
        Player target = Bukkit.getPlayer(otherPlayer);
        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }
        if (player.getName().equalsIgnoreCase(otherPlayer)) {
            player.sendMessage(CC.error("You can't spectate yourself!"));
            return;
        }

        Profile profile = API.getProfile(player);

        if (profile.getMatch() != null) {
            player.sendMessage(CC.error("You can't spectate while in a match!"));
            return;
        }

        Profile targetProfile = API.getProfile(target);
        if (targetProfile.getMatch() == null) {
            player.sendMessage(CC.error("Player isn't in a match!"));
            return;
        }

        if (!targetProfile.getSettingData().isAllowSpectators()) {
            MessagesLocale.SPECTATE_NOT_ALLOWED.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
            return;
        }

        targetProfile.getMatch().addSpectator(player, target, true);
    }

    @Subcommand("leave")
    public void leave(Player player) {
        Profile profile = API.getProfile(player);

        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            API.getProfile(player).getMatch().removeSpectator(player.getUniqueId(), true);
        }
    }

    @Subcommand("quit")
    public void quit(Player player) {
        Profile profile = API.getProfile(player);

        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            API.getProfile(player).getMatch().removeSpectator(player.getUniqueId(), true);
        }
    }
}