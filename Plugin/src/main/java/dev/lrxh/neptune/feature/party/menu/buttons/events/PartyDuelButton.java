package dev.lrxh.neptune.feature.party.menu.buttons.events;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.game.duel.menu.KitSelectMenu;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.util.*;

public class PartyDuelButton extends Button {
    private Party party;
    private Party targetParty;

    public PartyDuelButton(int slot, Party party, Party targetParty) {
        super(slot);
        this.party = party;
        this.targetParty = targetParty;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitSelectMenu(targetParty.getLeader(), true).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, party.getUsers().size());
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(player);
        itemStack.setItemMeta(meta);
        return new ItemBuilder(itemStack)
                .name(MenusLocale.PARTY_DUEL_PARTY_TITLE.getString().replaceAll("<leader>", party.getLeaderName()))
                .lore(Arrays.asList(getItemLore()), player)
                .build();
    }
    private String[] getItemLore() {
        List<String> lore = new ArrayList<>();
        for (UUID userUUID : party.getUsers()) {
            Player player = Bukkit.getPlayer(userUUID);
            if (player != null) {
                lore.add(MenusLocale.PARTY_DUEL_PARTY_MEMBER.getString().replace("<member>", player.getDisplayName()));
            }
        }
        return lore.toArray(new String[0]);
    }
}
