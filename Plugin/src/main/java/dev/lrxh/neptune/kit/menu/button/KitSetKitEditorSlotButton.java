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

public class KitSetKitEditorSlotButton extends Button {
    private final Kit kit;

    public KitSetKitEditorSlotButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.SET_KIT_EDITOR_SLOT);
        profile.getKitProcedure().setKit(kit);
        player.closeInventory();
        player.sendMessage(CC.info("Please enter new kit-editor slot"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.LADDER).name("&9Change kit-editor slot &7(" + kit.getKitEditorSlot() + ")").build();
    }
}
