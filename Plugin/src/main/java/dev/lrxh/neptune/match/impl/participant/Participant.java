package dev.lrxh.neptune.match.impl.participant;

import com.mongodb.lang.Nullable;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.sounds.Sound;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
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
    private Neptune plugin;

    public Participant(Player player, Neptune plugin) {
        this.playerUUID = player.getUniqueId();
        this.name = player.getName();
        this.plugin = plugin;
    }

    public String getNameColored() {
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
                (org.bukkit.Sound) plugin.getVersionHandler().getSound().getSound(sound), 1.0f, 1.0f);
    }

    public void playKillEffect() {
        UUID attckerUUID = getLastAttacker().getPlayerUUID();
        if (attckerUUID == null) return;
        Profile profile = plugin.getAPI().getProfile(attckerUUID);
        Player player = Bukkit.getPlayer(playerUUID);
        Player killer = Bukkit.getPlayer(attckerUUID);
        if (profile == null || player == null || killer == null) return;
        profile.getSettingData().getKillEffect().execute(player, killer);
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

    public void handleHit(Participant opponent) {
        Participant lastAttacker = opponent.getLastAttacker();
        if (lastAttacker != null && !lastAttacker.equals(this)) {
            resetCombo();
        }

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
        Match match = plugin.getAPI().getProfile(playerUUID).getMatch();
        if (match instanceof SoloFightMatch) {
            if (match.getKit().is(KitRule.BOXING)) {
                if (hits >= 100) {
                    opponent.setDeathCause(getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                    match.onDeath(opponent);
                }
            }
        }
    }

    public String getHitsDifference(Participant otherParticipant) {
        if (hits - otherParticipant.getHits() > 0) {
            return CC.color("&a(+" + (hits - otherParticipant.getHits()) + ")");
        } else if (hits - otherParticipant.getHits() < 0) {
            return CC.color("&c(" + (hits - otherParticipant.getHits()) + ")");
        } else {
            return CC.color("&e(" + (hits - otherParticipant.getHits()) + ")");
        }
    }

    public String getDeathMessage() {
        Profile profile = plugin.getAPI().getProfile(playerUUID);
        if (profile == null) {
            return "";
        }

        Match match = profile.getMatch();
        if (match == null) {
            return "";
        }

        Participant lastAttacker = getLastAttacker();
        if (lastAttacker == null) {
            return "";
        }

        Profile attackerProfile = plugin.getAPI().getProfile(lastAttacker.getPlayerUUID());
        if (attackerProfile == null || attackerProfile.getSettingData().getKillMessagePackage() == null) {
            return "";
        }

        return attackerProfile.getSettingData().getKillMessagePackage().getRandomMessage()
                .replace("<player>", getNameColored())
                .replace("<killer>", getLastAttackerName());
    }

    public String getLastAttackerName() {
        return Optional.ofNullable(getLastAttacker())
                .map(Participant::getNameColored)
                .orElse("");
    }

    @Nullable
    public Player getPlayer(){
        return Bukkit.getPlayer(playerUUID);
    }
}
