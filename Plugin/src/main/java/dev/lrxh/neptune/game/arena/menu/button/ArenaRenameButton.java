package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaRenameButton extends Button {
    private final Arena arena;

    public ArenaRenameButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setType(ArenaProcedureType.RENAME);
        profile.getArenaProcedure().setArena(arena);
        player.closeInventory();
        player.sendMessage(
                CC.info("<hover:show_text:\"<yellow>Click to paste current display name\">Please type new display name &8(Color codes can be used)")
                        .clickEvent(ClickEvent.suggestCommand(arena.getDisplayName()))
        );
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.NAME_TAG).name("&eRename arena &7(" + arena.getDisplayName() + "&7)").build();
    }
}
