package dev.lrxh.neptune.match.impl.participant;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;

@Getter
public enum ParticipantColor {
    RED(ChatColor.RED),
    BLUE(ChatColor.BLUE);

    private final ChatColor color;

    public Color getContentColor() {
        if (this.equals(RED)) return Color.RED;

        return Color.BLUE;
    }

    ParticipantColor(ChatColor color) {
        this.color = color;
    }
}
