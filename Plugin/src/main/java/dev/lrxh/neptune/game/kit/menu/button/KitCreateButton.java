package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.procedure.metadata.KitProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitCreateButton extends Button {

    public KitCreateButton(int slot) {
        super(slot, false);
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.CREATE);
        player.closeInventory();
        player.sendMessage(CC.info("Please type the kit name"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("&aCreate Kit").build();
    }
}
