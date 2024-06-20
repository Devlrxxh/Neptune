package dev.lrxh.neptune.match.impl.participant;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum ParticipantColor {
    RED(ChatColor.RED),
    BLUE(ChatColor.BLUE);

    private final ChatColor color;

    ParticipantColor(ChatColor color) {
        this.color = color;
    }
}
