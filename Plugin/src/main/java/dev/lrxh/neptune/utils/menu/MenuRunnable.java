package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class MenuRunnable extends NeptuneRunnable {
    @Override
    public void run() {
        for (Map.Entry<UUID, Menu> entry : MenuService.get().getOpenedMenus().entrySet()) {
            Menu menu = entry.getValue();
            if (!menu.isUpdateEveryTick()) continue;
            UUID playerUUID = entry.getKey();
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null || !player.isOnline()) {
                continue;
            }
            menu.update(player);
        }
    }
}
