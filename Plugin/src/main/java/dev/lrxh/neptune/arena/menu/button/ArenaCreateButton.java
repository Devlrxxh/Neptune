package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaCreateButton extends Button {

    public ArenaCreateButton(int slot) {
        super(slot, false);
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setType(ArenaProcedureType.CREATE);
        player.closeInventory();
        player.sendMessage(CC.info("&ePlease type the arena name"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("&aCreate arena").build();
    }
}
