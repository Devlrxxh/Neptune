package dev.lrxh.neptune.match.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockTracker implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) return;

        match.getChanges().put(event.getBlock().getLocation(), event.getBlockReplacedState().getBlockData());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) return;

        match.addChange(event.getBlock().getLocation(), event.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) return;

        match.getChanges().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }
}
