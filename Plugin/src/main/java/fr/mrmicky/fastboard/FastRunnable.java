package fr.mrmicky.fastboard;

import dev.lrxh.neptune.utils.CC;
import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@AllArgsConstructor
public class FastRunnable implements Runnable {
    private FastManager manager;

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, FastBoard>> iterator = manager.boards.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, FastBoard> entry = iterator.next();
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }

            FastBoard board = entry.getValue();
            board.updateTitle(CC.color(manager.fastAdapter.getTitle(player)));

            List<Component> lines = new ArrayList<>();
            for (String i : manager.fastAdapter.getLines(player)) {
                lines.add(CC.color(i));
            }
            board.updateLines(lines);
        }
    }
}
