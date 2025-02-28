package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.arena.impl.EdgeType;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class ArenaSetEdgeButton extends Button {
    private final StandAloneArena arena;
    private final EdgeType edgeType;

    public ArenaSetEdgeButton(int slot, StandAloneArena arena, EdgeType edgeType) {
        super(slot, false);
        this.arena = arena;
        this.edgeType = edgeType;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getArenaProcedure().setArena(arena);

        if (edgeType.equals(EdgeType.MIN)) {
            profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_MIN);
            player.sendMessage(CC.info("Go to the lowest edge of the arena and type &aDone"));
        } else {
            profile.getArenaProcedure().setType(ArenaProcedureType.SET_SPAWN_MAX);
            player.sendMessage(CC.info("Go to the highest edge of the arena and type &aDone"));
        }
        player.closeInventory();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (edgeType.equals(EdgeType.MIN)) {
            return new ItemBuilder(Material.BLUE_DYE).name("&9Set lowest edge")
                    .lore("&7Min: " + arena.getMin().getBlockX() + " Y: " + arena.getMin().getBlockY() + " Z: " + arena.getMin().getBlockZ()).build();
        }
        return new ItemBuilder(Material.RED_DYE).name("&cSet highest edge")
                .lore("&7X: " + arena.getMax().getBlockX() + " Y: " + arena.getMax().getBlockY() + " Z: " + arena.getMax().getBlockZ()).build();
    }
}
