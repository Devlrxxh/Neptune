package dev.lrxh.neptune.game.match.impl.participant;

import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.metadata.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.metadata.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.Time;
import lombok.Data;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Data
public class Participant implements IParticipant {

    // Player Info
    private final UUID playerUUID;
    private final String name;
    private ParticipantColor color;
    private Participant opponent;
    private Participant lastAttacker;

    private DeathCause deathCause;

    // Match State
    private boolean dead = false;
    private boolean loser = false;
    private boolean disconnected = false;
    private boolean left = false;
    private int points = 0;

    // Combat Stats
    private int hits;
    private int combo;
    private int longestCombo;
    private int eloChange = 0;

    // Game Rules / Mechanics
    private boolean frozen = false;
    private boolean bedBroken = false;
    private Time time;

    // Parkour Specific
    private Location currentCheckPoint;
    private int checkPoint = 0;

    public Participant(Player player) {
        this.playerUUID = player.getUniqueId();
        this.name = player.getName();
    }

    // --- Reset / State Management ---
    public void reset() {
        this.bedBroken = false;
        this.currentCheckPoint = null;
        this.checkPoint = 0;
        this.hits = 0;
        this.combo = 0;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
        playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
    }

    public void toggleFreeze() {
        this.frozen = !this.frozen;
    }

    public void resetCombo() {
        this.combo = 0;
    }

    public boolean setCurrentCheckPoint(Location location) {
        if (currentCheckPoint != null
                && location.getBlockX() == currentCheckPoint.getBlockX()
                && location.getBlockY() == currentCheckPoint.getBlockY()
                && location.getBlockZ() == currentCheckPoint.getBlockZ()) {
            return false;
        }
        this.currentCheckPoint = location.add(0.5, 0, 0.5);
        this.checkPoint++;
        return true;
    }

    // --- Getters ---
    public Player getPlayer() {
        Player p = Bukkit.getPlayer(playerUUID);
        return p != null ? p : Bukkit.getPlayer(name);
    }

    public String getNameColored() {
        return color.getColor() + name;
    }

    public String getNameUnColored() {
        return name;
    }

    public Profile getProfile() {
        return API.getProfile(playerUUID);
    }

    public Location getSpawn(Match match) {
        return currentCheckPoint != null ? currentCheckPoint : match.getSpawn(this);
    }

    public String getLastAttackerName() {
        return Optional.ofNullable(getLastAttacker())
                .map(Participant::getNameColored)
                .orElse("");
    }

    public String getDeathMessage() {
        Profile profile = getProfile();
        if (profile == null) return "";

        Match match = profile.getMatch();
        if (match == null) return "";

        Participant attacker = getLastAttacker();
        if (attacker == null) return "";

        Profile attackerProfile = API.getProfile(attacker.getPlayerUUID());
        if (attackerProfile == null || attackerProfile.getSettingData().getKillMessagePackage() == null)
            return "";

        return attackerProfile.getSettingData().getKillMessagePackage().getRandomMessage()
                .replace("<player>", getNameColored())
                .replace("<killer>", getLastAttackerName());
    }

    public void handleHit(Participant opponent) {
        if (opponent.getLastAttacker() != null && !opponent.getLastAttacker().equals(this)) {
            resetCombo();
        }

        hits++;
        combo++;
        if (combo > longestCombo) longestCombo = combo;

        if (MessagesLocale.MATCH_COMBO_MESSAGE_ENABLE.getBoolean()) {
            switch (combo) {
                case 5 -> MessagesLocale.MATCH_COMBO_MESSAGE_5.send(playerUUID);
                case 10 -> MessagesLocale.MATCH_COMBO_MESSAGE_10.send(playerUUID);
                case 20 -> MessagesLocale.MATCH_COMBO_MESSAGE_20.send(playerUUID);
            }
        }

        Match match = getProfile().getMatch();
        if (match.getKit().is(KitRule.BOXING)) {
            int maxHits = match instanceof TeamFightMatch teamFightMatch
                    ? teamFightMatch.getTeamA().getParticipants().size() * 100
                    : 100;
            if (hits >= maxHits) {
                opponent.setDeathCause(getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                match.onDeath(opponent);
            }
        }
    }

    public String getHitsDifference(Participant other) {
        int diff = hits - other.getHits();
        if (diff > 0) return "&a(+" + diff + ")";
        if (diff < 0) return "&c(" + diff + ")";
        return "&e(" + diff + ")";
    }

    public void playKillEffect() {
        Participant attacker = getLastAttacker();
        if (attacker == null) return;

        UUID attackerUUID = attacker.getPlayerUUID();
        if (attackerUUID == null) return;

        Profile profile = API.getProfile(attackerUUID);
        Player player = getPlayer();
        Player killer = Bukkit.getPlayer(attackerUUID);

        if (profile == null || player == null || killer == null) return;

        profile.getSettingData().getKillEffect().execute(player, killer);
    }

    public void sendTitle(TextComponent header, TextComponent footer, int duration) {
        Player player = getPlayer();
        if (player != null) PlayerUtil.sendTitle(player, header, footer, duration);
    }

    public void sendTitle(MessagesLocale header, MessagesLocale footer, int duration) {
        Player player = getPlayer();
        if (player != null) PlayerUtil.sendTitle(player, CC.color(header.getString()), CC.color(footer.getString()), duration);
    }

    public void sendMessage(TextComponent message) {
        PlayerUtil.sendMessage(playerUUID, message);
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        Player player = getPlayer();
        if (player != null) message.send(player, replacements);
    }

    public void playSound(Sound sound) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }

    public void addWin() {
        points++;
    }

    public void teleport(Location location) {
        Player player = getPlayer();
        if (player != null) player.teleport(location);
    }
}