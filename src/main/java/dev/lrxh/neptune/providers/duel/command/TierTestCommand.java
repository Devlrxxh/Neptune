package dev.lrxh.neptune.providers.duel.command;

import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


@CommandAlias("tiertest")
@CommandPermission("neptune.tier.test")
@Description("Tier test other players.")
public class TierTestCommand {

    @Default
    @Syntax("<name>")
    @CommandCompletion("@names")
    public void duel(Player player, String otherPlayer) {
        Player target = Bukkit.getPlayer(otherPlayer);
        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }

        if (Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (player.getName().equalsIgnoreCase(otherPlayer)) {
            player.sendMessage(CC.error("You can't duel yourself!"));
            return;
        }
        Profile targetProfile = Neptune.get().getProfileManager().getByUUID(target.getUniqueId());

        if (targetProfile.getMatch() != null || targetProfile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getGameData().getDuelRequest() != null) {
            player.sendMessage(CC.error("Player already has pending duel request!"));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), true).openMenu(player);
    }

}
