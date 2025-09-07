package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.procedure.metadata.KitProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitRenameButton extends Button {
    private final Kit kit;

    public KitRenameButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.RENAME);
        profile.getKitProcedure().setKit(kit);
        player.closeInventory();
        player.sendMessage(
                CC.info("<hover:show_text:\"<yellow>Click to paste current display name\">Please type new display name &8(Color codes can be used)")
                        .clickEvent(ClickEvent.suggestCommand(kit.getDisplayName()))
        );
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.NAME_TAG).name("&eRename kit &7(" + kit.getDisplayName() + "&7)").build();
    }
}
