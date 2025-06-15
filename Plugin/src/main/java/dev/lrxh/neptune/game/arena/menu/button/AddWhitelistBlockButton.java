package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class AddWhitelistBlockButton extends Button {
    private final StandAloneArena arena;

    public AddWhitelistBlockButton(int slot, StandAloneArena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setArena(arena);

        profile.getArenaProcedure().setType(ArenaProcedureType.ADD_BLOCK);
        player.sendMessage(CC.info("Hold the block you want to whitelist in main hand and type &aDone"));
        player.closeInventory();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("&aAdd Block").lore("&aPress to add").build();
    }
}
