package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Objects;
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
    private int hits;
    private int longestCombo;
    private int combo;

    public Participant(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.name = Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).getName();
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

    public void resetCombo() {
        combo = 0;
    }

    public void handleHit() {
        hits++;
        combo++;
        if (combo > longestCombo) {
            longestCombo = combo;
        }

        Match match = Neptune.get().getProfileManager().getByUUID(playerUUID).getMatch();
        if(match.getKit().isBoxing()){
            if(hits >= 100){
                if (match instanceof TeamFightMatch) {
                    setDeathCause(getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                    match.onDeath(this);
                }
            }
        }
    }
}
