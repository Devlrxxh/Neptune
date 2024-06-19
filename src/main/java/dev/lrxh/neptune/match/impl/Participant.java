package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.sounds.Sound;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

@Data
public class Participant {
    private UUID playerUUID;
    private String name;
    private Participant opponent;
    private DeathCause deathCause;
    private ParticipantColor color;
    private Participant lastAttacker;
    private int hits;
    private int longestCombo;
    private int combo;
    private boolean loser;
    private boolean disconnected = false;
    private int roundsWon = 0;

    public Participant(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.name = Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).getName();
    }

    public String getName() {
        return color.getColor() + name;
    }

    public void addWin() {
        roundsWon++;
    }

    public String getNameUnColored() {
        return name;
    }

    public void playSound(Sound sound) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        player.playSound(player.getLocation(),
                (org.bukkit.Sound) Neptune.get().getVersionHandler().getSound().getSound(sound), 1.0f, 1.0f);
    }

    public void sendTitle(String header, String footer, int duration) {
        PlayerUtil.sendTitle(playerUUID, header, footer, duration);
    }

    public void sendMessage(String message) {
        PlayerUtil.sendMessage(playerUUID, message);
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
        if (match instanceof SoloFightMatch) {
            if (match.getKit().isBoxing()) {
                if (hits >= 100) {
                    Participant opponent = getOpponent();

                    opponent.setDeathCause(getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                    match.onDeath(opponent);
                }
            }
        }
    }
}
