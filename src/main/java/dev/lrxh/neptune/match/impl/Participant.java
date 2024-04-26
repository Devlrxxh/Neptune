package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
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

        if (MessagesLocale.MATCH_COMBO_MESSAGE_ENABLE.getBoolean()) {
            switch (combo) {
                case 5:
                    MessagesLocale.MATCH_COMBO_MESSAGE_5.send(playerUUID);
                    break;
                case 10:
                    MessagesLocale.MATCH_COMBO_MESSAGE_10.send(playerUUID);
                    break;
                case 20:
                    MessagesLocale.MATCH_COMBO_MESSAGE_20.send(playerUUID);
                    break;
            }
        }
        Match match = Neptune.get().getProfileManager().getByUUID(playerUUID).getMatch();
        if (match instanceof TeamFightMatch) {
            if (match.getKit().isBoxing()) {
                if (hits >= 100) {

                    Participant opponent = match.getParticipant(
                            getOpponent().getParticipants().get(getOpponent().getParticipants().size() - 1).getPlayerUUID());

                    opponent.setDeathCause(getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                    match.onDeath(opponent);
                }
            }
        }
    }
}
