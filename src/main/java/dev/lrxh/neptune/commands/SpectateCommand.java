package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
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

        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());

        if (profile.getMatch() != null) {
            player.sendMessage(CC.error("You can't spectate while in a match!"));
            return;
        }

        Profile targetProfile = Neptune.get().getProfileManager().getByUUID(target.getUniqueId());
        if (targetProfile.getMatch() == null) {
            player.sendMessage(CC.error("Player isn't in a match!"));
            return;
        }

        targetProfile.getMatch().addSpectator(player.getUniqueId(), true);
        player.teleport(target);
    }

    @Subcommand("leave")
    public void leave(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());

        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getMatch().removeSpectator(player.getUniqueId(), true);
        }
    }

    @Subcommand("quit")
    public void quit(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());

        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getMatch().removeSpectator(player.getUniqueId(), true);
        }
    }
}