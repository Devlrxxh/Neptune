package fr.mrmicky.fastboard;

import dev.lrxh.neptune.utils.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class FastRunnable implements Runnable {
    private FastManager manager;

    @Override
    public void run() {
        for (Map.Entry<UUID, FastBoard> entry : manager.boards.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            FastBoard board = entry.getValue();
            board.updateTitle(CC.color(manager.fastAdapter.getTitle(player)));

            List<String> lines = new ArrayList<>();

            for (String i : manager.fastAdapter.getLines(player)) {
                lines.add(CC.color(i));
            }
            board.updateLines(lines);
        }
    }
}
