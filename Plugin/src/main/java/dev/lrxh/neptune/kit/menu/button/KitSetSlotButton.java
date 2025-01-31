package dev.lrxh.neptune.kit.menu.button;

import dev.lrxh.neptune.API;
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

public class KitSetSlotButton extends Button {
    private final Kit kit;

    public KitSetSlotButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.SET_SLOT);
        profile.getKitProcedure().setKit(kit);
        player.closeInventory();
        player.sendMessage(CC.info("Please enter new slot"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.IRON_DOOR).name("&9Change slot &7(" + kit.getSlot() + ")").build();
    }
}
