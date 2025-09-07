package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class QueueCheckTask extends NeptuneRunnable {

    @Override
    public void run() {
        sendQueueActionBars();
        processQueues();
    }

    /**
     * Sends action bar messages to all players currently queued.
     */
    private void sendQueueActionBars() {
        for (Queue<QueueEntry> queue : QueueService.get().getAllQueues().values()) {
            for (QueueEntry entry : queue) {
                Player player = Bukkit.getPlayer(entry.getUuid());
                if (player != null) {
                    String message = PlaceholderUtil.format(MessagesLocale.QUEUE_ACTION_BAR.getString(), player);
                    player.sendActionBar(CC.color(message));
                }
            }
        }
    }

    /**
     * Processes each kit queue and attempts to create matches.
     */
    private void processQueues() {
        for (Map.Entry<Kit, Queue<QueueEntry>> entry : QueueService.get().getAllQueues().entrySet()) {
            Kit kit = entry.getKey();
            Queue<QueueEntry> kitQueue = entry.getValue();

            if (kitQueue.size() < 2) continue;

            QueueEntry entry1 = QueueService.get().poll(kit);
            QueueEntry entry2 = QueueService.get().poll(kit);

            if (entry1 == null || entry2 == null) continue;

            attemptMatch(entry1, entry2, kit);
        }
    }

    /**
     * Attempts to create a match between two queue entries.
     *
     * @param entry1 the first queue entry
     * @param entry2 the second queue entry
     * @param kit    the kit they are queued for
     */
    private void attemptMatch(QueueEntry entry1, QueueEntry entry2, Kit kit) {
        UUID uuid1 = entry1.getUuid();
        UUID uuid2 = entry2.getUuid();

        Profile profile1 = API.getProfile(uuid1);
        Profile profile2 = API.getProfile(uuid2);

        if (!entry1.getKit().equals(entry2.getKit())) {
            requeue(entry1, entry2);
            return;
        }

        if (!pingWithinLimits(uuid1, profile1, uuid2, profile2)) {
            requeue(entry1, entry2);
            return;
        }

        Player player1 = Bukkit.getPlayer(uuid1);
        Player player2 = Bukkit.getPlayer(uuid2);
        if (player1 == null || player2 == null) return;

        profile1.setState(ProfileState.IN_LOBBY);
        profile2.setState(ProfileState.IN_LOBBY);

        kit.getRandomArena().thenAccept(arena -> {
            if (arena == null) {
                PlayerUtil.sendMessage(uuid1, CC.error("No valid arena was found for this kit!"));
                PlayerUtil.sendMessage(uuid2, CC.error("No valid arena was found for this kit!"));
                return;
            }
            startMatch(player1, player2, profile1, profile2, kit, arena);
        });
    }

    /**
     * Re-queues two entries back into their kit queue.
     */
    private void requeue(QueueEntry... entries) {
        for (QueueEntry entry : entries) {
            QueueService.get().add(entry, false);
        }
    }

    /**
     * Validates if both players are within each other’s max ping limits.
     */
    private boolean pingWithinLimits(UUID uuid1, Profile profile1, UUID uuid2, Profile profile2) {
        int ping1 = PlayerUtil.getPing(uuid1);
        int ping2 = PlayerUtil.getPing(uuid2);

        SettingData settings1 = profile1.getSettingData();
        SettingData settings2 = profile2.getSettingData();

        return ping2 <= settings1.getMaxPing() && ping1 <= settings2.getMaxPing();
    }

    /**
     * Starts a new match between two players in the given kit and arena.
     */
    private void startMatch(Player player1,
                            Player player2,
                            Profile profile1,
                            Profile profile2,
                            Kit kit,
                            Arena arena) {

        Participant participant1 = new Participant(player1);
        Participant participant2 = new Participant(player2);
        List<Participant> participants = Arrays.asList(participant1, participant2);

        int ping1 = PlayerUtil.getPing(player1.getUniqueId());
        int ping2 = PlayerUtil.getPing(player2.getUniqueId());

        MessagesLocale.MATCH_FOUND.send(
                player1.getUniqueId(),
                new Replacement("<opponent>", participant2.getNameUnColored()),
                new Replacement("<kit>", kit.getDisplayName()),
                new Replacement("<arena>", arena.getDisplayName()),
                new Replacement("<opponent-ping>", String.valueOf(ping2)),
                new Replacement("<opponent-elo>", String.valueOf(profile2.getGameData().get(kit).getElo())),
                new Replacement("<elo>", String.valueOf(profile1.getGameData().get(kit).getElo())),
                new Replacement("<ping>", String.valueOf(ping1))
        );

        MessagesLocale.MATCH_FOUND.send(
                player2.getUniqueId(),
                new Replacement("<opponent>", participant1.getNameUnColored()),
                new Replacement("<kit>", kit.getDisplayName()),
                new Replacement("<arena>", arena.getDisplayName()),
                new Replacement("<opponent-ping>", String.valueOf(ping1)),
                new Replacement("<opponent-elo>", String.valueOf(profile1.getGameData().get(kit).getElo())),
                new Replacement("<elo>", String.valueOf(profile2.getGameData().get(kit).getElo())),
                new Replacement("<ping>", String.valueOf(ping2))
        );

        Bukkit.getScheduler().runTask(Neptune.get(), () -> {
            MatchService.get().startMatch(
                    participants,
                    kit,
                    arena,
                    false,
                    kit.is(KitRule.BEST_OF_THREE) ? 3 : 1
            );
        });
    }
}