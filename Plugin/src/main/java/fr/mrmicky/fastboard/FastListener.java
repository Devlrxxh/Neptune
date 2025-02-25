package fr.mrmicky.fastboard;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class FastListener implements Listener {
    private final FastManager manager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        manager.boards.put(player.getUniqueId(), new FastBoard(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        FastBoard board = manager.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }
}
