package dev.lrxh.neptune.listeners;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Objects;

public class LobbyListener implements Listener {

    private final Neptune plugin;

    public LobbyListener() {
        this.plugin = Neptune.get();
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType().equals(Material.GLASS_BOTTLE)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onSoilChange(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.FARMLAND)
            event.setCancelled(true);
    }
}
