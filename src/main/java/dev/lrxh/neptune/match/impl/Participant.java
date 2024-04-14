package dev.lrxh.neptune.match.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Participant {
    private UUID playerUUID;
    private String name;
    private Team opponent;
    private DeathCause deathCause;
    private ParticipantColor color;
    private Participant lastAttacker;
    private boolean dead;

    public Participant(UUID playerUUID, String name) {
        this.playerUUID = playerUUID;
        this.name = name;
        this.dead = false;
    }


    public String getName() {
        return color.getColor() + name;
    }

    public String getNameUnColored() {
        return name;
    }

    public void playSound(Sound sound) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }
}
