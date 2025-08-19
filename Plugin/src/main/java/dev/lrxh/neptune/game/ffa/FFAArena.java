package dev.lrxh.neptune.game.ffa;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
@Setter
public class FFAArena {
    private String name;
    private List<Kit> allowedKits;
    private HashMap<String, Location> spawnLocations;

    private HashMap<UUID, Kit> players;
    private HashMap<UUID, Integer> killStreak;

    public FFAArena(String name, List<Kit> allowedKits, HashMap<String, Location> spawnLocations) {
        this.name = name;
        this.allowedKits = allowedKits;
        this.spawnLocations = spawnLocations;

        this.players = new HashMap<>();
        this.killStreak = new HashMap<>();
    }

    public void addPlayer(Profile profile, Kit kit, String location) {
        if (!allowedKits.contains(kit)) return;
        players.put(profile.getPlayerUUID(), kit);
        profile.getGameData().setFfaArena(this);
        killStreak.put(profile.getPlayerUUID(), 0);
        profile.setState(ProfileState.IN_FFA);
        kit.giveLoadout(profile.getPlayerUUID());
        for (String loc : spawnLocations.keySet() ) {
            if (loc.equalsIgnoreCase(location)) {
                profile.getPlayer().teleport(spawnLocations.get(loc));
                return;
            }
        }
        profile.getGameData().setLastPlayedArena(this);
        profile.getGameData().setLastPlayedKitInFfa(getPlayerKit(profile.getPlayerUUID()));
        profile.getGameData().setLastPlayedSpawn(location);

        if (!kit.getRules().get(KitRule.SATURATION)) {
            profile.getPlayer().setSaturation(0);
        }
    }

    public void removePlayer(Profile profile) {
        players.remove(profile.getPlayerUUID());
        profile.getGameData().setFfaArena(null);
        profile.setState(ProfileState.IN_LOBBY);
        PlayerUtil.teleportToSpawn(profile.getPlayerUUID());
        killStreak.remove(profile.getPlayerUUID());
    }

    public void onDeath(Profile killer, Profile victim) {
        if (killer == null || victim == null) return;
        if (killer.getGameData().getFfaArena() != this) return;
        if (victim.getGameData().getFfaArena() != this) return;

        var killerKitData = killer.getGameData().getKitData().get(players.get(killer.getPlayerUUID()));
        killerKitData.setFfaKills(killerKitData.getFfaKills() + 1);

        var victimKitData = victim.getGameData().getKitData().get(players.get(victim.getPlayerUUID()));
        victimKitData.setFfaDeaths(victimKitData.getFfaDeaths() + 1);

        int streak = getKillStreak().getOrDefault(killer.getPlayerUUID(), 0) + 1;
        getKillStreak().put(killer.getPlayerUUID(), streak);
        getKillStreak().put(victim.getPlayerUUID(), 0); // Reset victim's killstreak

        if (killer.getGameData().getKitData().get(players.get(killer.getPlayerUUID())).getFfaBestStreak() < streak) {
            killer.getGameData().getKitData().get(players.get(killer.getPlayerUUID())).setFfaBestStreak(streak);
        }

        // Always send a random kill message
        List<String> killMessages = MessagesLocale.FFA_KILL_ANNOUNCE.getStringList();
        Random random = new Random();
        String randomKillMessage = killMessages.get(random.nextInt(killMessages.size()));
        String formattedKillMsg = randomKillMessage
                .replace("<player>", victim.getPlayer().getName())
                .replace("<killer>", killer.getPlayer().getName())
                .replace("<hpLeft>", String.format("%.1f", killer.getPlayer().getHealth()))
                .replace("<killstreak>", String.valueOf(streak));

        for (UUID uuid : getAllPlayers()) {
            Bukkit.getPlayer(uuid).sendMessage(CC.color(formattedKillMsg));
        }

        // Check if a separate killstreak announcement should be sent
        boolean meetsRule = false;
        String rules = MessagesLocale.FFA_KILLSTREAK_ANNOUNCE_RULES.getString();
        for (String rule : rules.split(",")) {
            if (shouldAnnounce(rule.trim(), streak)) {
                meetsRule = true;
                break;
            }
        }

        if (MessagesLocale.FFA_KILLSTREAK_ANNOUNCE_ENABLED.getBoolean() && meetsRule) {
            for (String line : MessagesLocale.FFA_KILLSTREAK_ANNOUNCE_MESSAGE.getStringList()) {
                String streakMsg = line
                        .replace("<player>", killer.getPlayer().getName())
                        .replace("<killstreak>", String.valueOf(streak));
                for (UUID uuid : getAllPlayers()) {
                    Bukkit.getPlayer(uuid).sendMessage(CC.color(streakMsg));
                }
            }
        }

        removePlayer(victim);
    }

    private static boolean shouldAnnounce(String rule, int streak) {
        if (rule.startsWith(">")) {
            int num = Integer.parseInt(rule.substring(1));
            return streak > num;
        } else if (rule.startsWith("<")) {
            int num = Integer.parseInt(rule.substring(1));
            return streak < num;
        } else if (rule.startsWith("+")) {
            int step = Integer.parseInt(rule.substring(1));
            return streak > 0 && streak % step == 0;
        } else {
            try {
                int num = Integer.parseInt(rule);
                return streak == num;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
    }

    public Kit getPlayerKit(UUID uuid) {
        return players.get(uuid);
    }

    public List<UUID> getAllPlayers() {
        return List.copyOf(players.keySet());
    }

    public int getKillStreak(UUID uuid) {
        return killStreak.getOrDefault(uuid, 0);
    }
}