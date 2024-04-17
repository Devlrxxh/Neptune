package dev.lrxh.neptune.match;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public abstract class Match {
    private final UUID uuid = UUID.randomUUID();
    public MatchState matchState;
    public Arena arena;
    public Kit kit;
    public List<Participant> participants;
    private boolean ranked, duel;

    public void playSound(Sound sound) {
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public Participant getParticipant(UUID playerUUID) {
        for (Participant participant : participants) {
            if (participant.getPlayerUUID().equals(playerUUID)) {
                return participant;
            }
        }
        return null;
    }

    public void sendTitle(String header, String footer, int duration) {
        for (Participant participant : participants) {
            PlayerUtils.sendTitle(participant.getPlayerUUID(), header, footer, duration);
        }
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        for (Participant participant : participants) {
            message.send(participant.getPlayerUUID(), replacements);
        }
    }

    public void checkRules() {
        for (Participant participant : participants) {
            if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) continue;
            if (kit.isDenyMovement()) {
                if (matchState.equals(MatchState.STARTING)) {
                    PlayerUtils.denyMovement(participant.getPlayerUUID());
                } else {
                    Bukkit.getPlayer(participant.getPlayerUUID()).resetTitle();
                    PlayerUtils.allowMovement(participant.getPlayerUUID());
                }
            }
        }
    }

    public void hidePlayer(Participant targetParticipant) {
        if (Bukkit.getPlayer(targetParticipant.getPlayerUUID()) == null) return;
        Player targetPlayer = Bukkit.getPlayer(targetParticipant.getPlayerUUID());
        for (Participant participant : participants) {
            if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) return;
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            player.hidePlayer(targetPlayer);
        }
    }


    public void giveKit() {
        for (Participant participant : participants) {
            if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) continue;
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
            player.getInventory().setArmorContents(kit.getArmour().toArray(new ItemStack[0]));
        }


    }

    public abstract void end();

    public abstract void onDeath(Participant participant);

    public abstract void respawn(Participant participant);

}
