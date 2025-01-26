package dev.lrxh.neptune.duel.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelCommand {

    @Command(name = "", desc = "", usage = "<player>")
    public void duel(@Sender Player player, Player target) {
        if (API.getProfile(player).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (player.getName().equalsIgnoreCase(target.getName())) {
            player.sendMessage(CC.error("You can't duel yourself!"));
            return;
        }
        Profile targetProfile = API.getProfile(target);

        if (targetProfile.getMatch() != null || targetProfile.hasState(ProfileState.IN_KIT_EDITOR) || !targetProfile.getSettingData().isAllowDuels()) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.DUEL_ALREADY_SENT.send(player.getUniqueId(), new Replacement("<player>", player.getName()));
            return;
        }

        Profile userProfile = API.getProfile(player);
        if (userProfile.getState().equals(ProfileState.IN_PARTY) && !targetProfile.getState().equals(ProfileState.IN_PARTY) || targetProfile.getState().equals(ProfileState.IN_PARTY) && !userProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (targetProfile.getState().equals(ProfileState.IN_PARTY) && !targetProfile.getGameData().getParty().isLeader(target.getUniqueId())) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), userProfile.getState().equals(ProfileState.IN_PARTY)).open(player);
    }

    @Command(name = "accept", desc = "", usage = "<uuid>")
    public void accept(@Sender Player player, UUID uuid) {
        Profile profile = API.getProfile(player);
        GameData playerGameData = API.getProfile(player).getGameData();

        if (profile.getMatch() != null || profile.getState().equals(ProfileState.IN_SPECTATOR) || profile.hasState(ProfileState.IN_KIT_EDITOR) || profile.hasState(ProfileState.IN_QUEUE)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }
        Profile targetProfile = API.getProfile(uuid);

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(uuid);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        if (!duelRequest.isParty() && targetProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }

        profile.acceptDuel(uuid);
    }

    @Command(name = "deny", desc = "", usage = "<uuid>")
    public void deny(@Sender Player player, UUID uuid) {
        Profile profile = API.getProfile(player);
        GameData playerGameData = profile.getGameData();

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(uuid);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        MessagesLocale.DUEL_DENY_SENDER.send(player.getUniqueId());
        MessagesLocale.DUEL_DENY_RECEIVER.send(uuid,
                new Replacement("<player>", player.getName()));

        playerGameData.removeRequest(uuid);
    }
}