package dev.lrxh.neptune.utils.assemble;

import org.bukkit.entity.Player;

import java.util.List;

public interface AssembleAdapter {

    /**
     * Get's the scoreboard title.
     *
     * @param player who's title is being displayed.
     * @return title.
     */
    String getTitle(Player player);

    /**
     * Get's the scoreboard lines.
     *
     * @param player who's lines are being displayed.
     * @return lines.
     */
    List<String> getLines(Player player);

}
