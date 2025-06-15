package dev.lrxh.neptune.game.match.impl.participant;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import lombok.Getter;
import org.bukkit.Color;

@Getter
public enum ParticipantColor {
    RED(SettingsLocale.PARTICIPANT_COLOR_RED.getString()),
    BLUE(SettingsLocale.PARTICIPANT_COLOR_BLUE.getString());

    private final String color;

    ParticipantColor(String color) {
        this.color = color;
    }

    public Color getContentColor() {
        if (this.equals(RED)) return Color.RED;

        return Color.BLUE;
    }
}
