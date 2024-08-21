package dev.lrxh.neptune.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("duel|1v1")
@Description("Duel other players.")
public class DuelCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Default
    @Syntax("<name>")
    @CommandCompletion("@names")
    public void duel(Player player, String otherPlayer) {
        Player target = Bukkit.getPlayer(otherPlayer);
        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }

        if (plugin.getAPI().getProfile(player).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (player.getName().equalsIgnoreCase(otherPlayer)) {
            player.sendMessage(CC.error("You can't duel yourself!"));
            return;
        }
        Profile targetProfile = plugin.getAPI().getProfile(target);

        if (targetProfile.getMatch() != null || targetProfile.hasState(ProfileState.IN_KIT_EDITOR) || !targetProfile.getSettingData().isAllowDuels()) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.DUEL_ALREADY_SENT.send(player.getUniqueId(), new Replacement("<player>", player.getName()));
            return;
        }

        Profile userProfile = plugin.getAPI().getProfile(player);
        if (userProfile.getState().equals(ProfileState.IN_PARTY) && !targetProfile.getState().equals(ProfileState.IN_PARTY) || targetProfile.getState().equals(ProfileState.IN_PARTY) && !userProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), userProfile.getState().equals(ProfileState.IN_PARTY)).openMenu(player.getUniqueId());
    }

    @Subcommand("accept")
    @Syntax("<uuid>")
    public void accept(Player player, String otherString) {
        Profile profile = plugin.getAPI().getProfile(player);
        GameData playerGameData = plugin.getAPI().getProfile(player).getGameData();

        if (profile.getMatch() != null || profile.getState().equals(ProfileState.IN_SPECTATOR) || profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }
        UUID targetUUID = UUID.fromString(otherString);
        Profile targetProfile = plugin.getAPI().getProfile(targetUUID);

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(targetUUID);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        if (!duelRequest.isParty() && targetProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }

        profile.acceptDuel(targetUUID);
    }

    @Subcommand("deny")
    @Syntax("<uuid>")
    public void deny(Player player, String otherString) {
        Profile profile = plugin.getAPI().getProfile(player);
        GameData playerGameData = profile.getGameData();

        UUID otherUUID = UUID.fromString(otherString);

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(otherUUID);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        MessagesLocale.DUEL_DENY_SENDER.send(player.getUniqueId());
        MessagesLocale.DUEL_DENY_RECEIVER.send(otherUUID,
                new Replacement("<player>", player.getName()));

        playerGameData.removeRequest(otherUUID);
    }
}