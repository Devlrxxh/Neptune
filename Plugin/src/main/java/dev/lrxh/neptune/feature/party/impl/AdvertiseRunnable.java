package dev.lrxh.neptune.feature.party.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.impl.advertise.AdvertiseService;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import net.kyori.adventure.text.TextComponent;

public class AdvertiseRunnable extends NeptuneRunnable {
    private final Party party;
    public AdvertiseRunnable(Party party) {
        this.party = party;
    }
    @Override
    public void run() {
        if (!AdvertiseService.has(this)) {
            this.stop();
            return;
        }
        if (!party.isOpen()) AdvertiseService.remove(party);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = API.getProfile(player);
            if (profile == null) return;
            if (profile.getGameData().getParty() == party) continue;
            SettingData data = profile.getSettingData();
            if (!data.isPartyAdvertisements()) continue;
            TextComponent join = new ClickableComponent(MessagesLocale.PARTY_ADVERTISE_JOIN.getString(), "/party join " + party.getLeaderName(), MessagesLocale.PARTY_ADVERTISE_JOIN_HOVER.getString().replaceAll("<leader>", party.getLeaderName())).build();
            MessagesLocale.PARTY_ADVERTISE_MESSAGE.send(player, new Replacement("<join>", join), new Replacement("<leader>", party.getLeaderName()));
        }
    }
}
