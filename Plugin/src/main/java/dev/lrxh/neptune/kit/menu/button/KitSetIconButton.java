package dev.lrxh.neptune.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.procedure.KitProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSetIconButton extends Button {
    private final Kit kit;

    public KitSetIconButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.SET_ICON);
        profile.getKitProcedure().setKit(kit);
        player.closeInventory();
        player.sendMessage(CC.info("Hold the the item in your press and type &aDone"));

    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.PAINTING).name("&dSet Icon").build();
    }
}
