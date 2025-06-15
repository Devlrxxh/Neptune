package fr.mrmicky.fastboard;

import dev.lrxh.neptune.utils.CC;
import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
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

            List<Component> lines = new ArrayList<>();

            for (String i : manager.fastAdapter.getLines(player)) {
                lines.add(CC.color(i));
            }
            board.updateLines(lines);
        }
    }
}
