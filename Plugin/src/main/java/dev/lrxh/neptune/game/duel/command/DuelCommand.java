package dev.lrxh.neptune.game.duel.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
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

        if (targetProfile.getMatch() != null || targetProfile.hasState(ProfileState.IN_KIT_EDITOR)
                || !targetProfile.getSettingData().isAllowDuels()) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.DUEL_ALREADY_SENT.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
            return;
        }

        Profile userProfile = API.getProfile(player);
        if (userProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getState().equals(ProfileState.IN_PARTY)
                || targetProfile.getState().equals(ProfileState.IN_PARTY)
                        && !userProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (targetProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getGameData().getParty().isLeader(target.getUniqueId())) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        new KitSelectMenu(target.getUniqueId(), userProfile.getState().equals(ProfileState.IN_PARTY)).open(player);
    }

    @Command(name = "accept-uuid", desc = "", usage = "<uuid>", hidden = true)
    public void acceptUUID(@Sender Player player, UUID uuid) {
        Profile profile = API.getProfile(player);
        GameData playerGameData = API.getProfile(player).getGameData();

        if (profile.getMatch() != null || profile.getState().equals(ProfileState.IN_SPECTATOR)
                || profile.hasState(ProfileState.IN_KIT_EDITOR) || profile.hasState(ProfileState.IN_QUEUE)) {
            player.sendMessage(CC.error("You can't accept duel requests right now!"));
            return;
        }

        Player target = Bukkit.getPlayer(uuid);

        if (target == null) {
            player.sendMessage(CC.error("Player isn't online!"));
            return;
        }

        Profile targetProfile = API.getProfile(target);

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(uuid);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        if (!duelRequest.isParty() && targetProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("Duel request couldn't be accepted!"));
            return;
        }

        if (duelRequest.isParty() && !profile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("Duel request couldn't be accepted!"));
            return;
        }

        if (duelRequest.isParty() && !targetProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("Duel request couldn't be accepted!"));
            return;
        }

        profile.acceptDuel(uuid);
    }

    @Command(name = "specific", desc = "", usage = "<player> <kit> <rounds>")
    public void duel(@Sender Player player, Player target, Kit kit, int rounds) {
        if (API.getProfile(player).getMatch() != null) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (player.getName().equalsIgnoreCase(target.getName())) {
            player.sendMessage(CC.error("You can't duel yourself!"));
            return;
        }
        Profile targetProfile = API.getProfile(target);

        if (targetProfile.getMatch() != null || targetProfile.hasState(ProfileState.IN_KIT_EDITOR)
                || !targetProfile.getSettingData().isAllowDuels()) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        if (targetProfile.getGameData().getRequests().contains(player.getUniqueId())) {
            MessagesLocale.DUEL_ALREADY_SENT.send(player.getUniqueId(), new Replacement("<player>", target.getName()));
            return;
        }

        Profile userProfile = API.getProfile(player);
        if (userProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getState().equals(ProfileState.IN_PARTY)
                || targetProfile.getState().equals(ProfileState.IN_PARTY)
                        && !userProfile.getState().equals(ProfileState.IN_PARTY)) {
            player.sendMessage(CC.error("You can't send duel requests right now!"));
            return;
        }

        if (targetProfile.getState().equals(ProfileState.IN_PARTY)
                && !targetProfile.getGameData().getParty().isLeader(target.getUniqueId())) {
            player.sendMessage(CC.error("Player can't accept duel requests!"));
            return;
        }

        boolean party = API.getProfile(player).getState().equals(ProfileState.IN_PARTY)
                && API.getProfile(target).getState().equals(ProfileState.IN_PARTY);

        kit.getRandomArena().thenAccept(arena -> {
            DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, arena, party, rounds);
            ProfileService.get().getByUUID(target.getUniqueId()).sendDuel(duelRequest);
        });
    }

    @Command(name = "deny-uuid", desc = "", usage = "<uuid>", hidden = true)
    public void denyUUID(@Sender Player player, UUID uuid) {
        Profile profile = API.getProfile(player);
        GameData playerGameData = profile.getGameData();

        DuelRequest duelRequest = (DuelRequest) playerGameData.getRequests().get(uuid);

        if (duelRequest == null) {
            player.sendMessage(CC.error("You don't have any duel request from this player!"));
            return;
        }

        Player sender = Bukkit.getPlayer(uuid);
        if (sender == null)
            return;

        MessagesLocale.DUEL_DENY_SENDER.send(player.getUniqueId(), new Replacement("<player>", sender.getName()));
        MessagesLocale.DUEL_DENY_RECEIVER.send(uuid,
                new Replacement("<player>", player.getName()));

        playerGameData.removeRequest(uuid);
    }
}