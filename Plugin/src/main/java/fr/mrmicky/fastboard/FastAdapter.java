package fr.mrmicky.fastboard;

import org.bukkit.entity.Player;

import java.util.List;

public interface FastAdapter {
    String getTitle(Player player);
    List<String> getLines(Player player);
}
