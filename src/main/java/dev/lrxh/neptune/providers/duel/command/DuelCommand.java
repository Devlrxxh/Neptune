package dev.lrxh.neptune.providers.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import dev.lrxh.neptune.providers.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("duel|1v1")
@Description("Duel other players.")
public class DuelCommand extends BaseCommand {

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
        GameData targetGameData = Neptune.get().getProfileManager().getByUUID(target.getUniqueId()).getGameData();

        if (targetProfile.getMatch() != null || targetProfile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetGameData.getDuelRequest() != null) {
            player.sendMessage(CC.error("Player already has pending duel request!"));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), false).openMenu(player);
    }

    @Subcommand("accept")
    public void accept(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());
        GameData playerGameData = Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getGameData();

        if (profile.getMatch() != null || profile.getState().equals(ProfileState.IN_SPECTATOR) || profile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }

        DuelRequest duelRequest = playerGameData.getDuelRequest();

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any pending duel request!"));
            return;
        }

        if (Bukkit.getPlayer(duelRequest.getSender()) == null) {
            player.sendMessage(CC.error("You don't have any pending duel request!"));
            playerGameData.setDuelRequest(null);
            return;
        }

        profile.acceptDuel();
    }

    @Subcommand("deny")
    public void deny(Player player) {
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());
        GameData playerGameData = Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getGameData();

        DuelRequest duelRequest = playerGameData.getDuelRequest();

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any pending duel request!"));
            return;
        }

        if (Bukkit.getPlayer(duelRequest.getSender()) == null || playerGameData.getDuelRequest() == null) {
            player.sendMessage(CC.error("You don't have any pending duel request!"));
            playerGameData.setDuelRequest(null);
            return;
        }

        player.sendMessage(CC.color("&cDuel request denied."));

        playerGameData.setDuelRequest(null);
    }
}