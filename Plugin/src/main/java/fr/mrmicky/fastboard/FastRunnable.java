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

            String rawTitle = manager.fastAdapter.getTitle(player);
            List<String> rawLines = manager.fastAdapter.getLines(player);

            String currentTitle = board.getTitle().toString();
            if (!currentTitle.equals(rawTitle)) {
                board.updateTitle(CC.color(rawTitle));
            }

            List<String> currentRawLines = board.getLines().stream()
                    .map(Component::toString)
                    .toList();

            if (!currentRawLines.equals(rawLines)) {
                List<Component> newLines = new ArrayList<>();
                for (String line : rawLines) {
                    newLines.add(CC.color(line));
                }
                board.updateLines(newLines);
            }
        }
    }

}
