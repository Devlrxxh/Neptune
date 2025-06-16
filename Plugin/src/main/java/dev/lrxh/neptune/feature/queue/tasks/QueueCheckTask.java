package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.neptune.API;
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
        for (Queue<QueueEntry> queue : QueueService.get().getAllQueues().values()) {
            for (QueueEntry entry : queue) {
                Player player = Bukkit.getPlayer(entry.getUuid());
                if (player != null) {
                    player.sendActionBar(CC.color(PlaceholderUtil.format(MessagesLocale.QUEUE_ACTION_BAR.getString(), player)));
                }
            }
        }


        for (Map.Entry<Kit, Queue<QueueEntry>> entry : QueueService.get().getAllQueues().entrySet()) {
            Kit kit = entry.getKey();
            Queue<QueueEntry> kitQueue = entry.getValue();

            while (kitQueue.size() >= 2) {
                QueueEntry queueEntry1 = kitQueue.poll();
                QueueEntry queueEntry2 = kitQueue.poll();

                if (queueEntry1 == null || queueEntry2 == null) break;

                UUID uuid1 = queueEntry1.getUuid();
                UUID uuid2 = queueEntry2.getUuid();

                Profile profile1 = API.getProfile(uuid1);
                Profile profile2 = API.getProfile(uuid2);

                SettingData settings1 = profile1.getSettingData();
                SettingData settings2 = profile2.getSettingData();

                // Ping check
                if (!(PlayerUtil.getPing(uuid2) <= settings1.getMaxPing() &&
                        PlayerUtil.getPing(uuid1) <= settings2.getMaxPing())) {
                    QueueService.get().add(queueEntry1, false);
                    QueueService.get().add(queueEntry2, false);
                    continue;
                }

                Player player1 = Bukkit.getPlayer(uuid1);
                Player player2 = Bukkit.getPlayer(uuid2);
                if (player1 == null || player2 == null) continue;

                Arena arena = kit.getRandomArena();
                if (arena == null || !arena.isSetup()) {
                    profile1.setState(ProfileState.IN_LOBBY);
                    profile2.setState(ProfileState.IN_LOBBY);
                    kit.removeQueue(); // Only call once per player if needed
                    kit.removeQueue();
                    PlayerUtil.sendMessage(uuid1, CC.error("No valid arena was found for this kit!"));
                    PlayerUtil.sendMessage(uuid2, CC.error("No valid arena was found for this kit!"));
                    continue;
                }

                Participant participant1 = new Participant(player1);
                Participant participant2 = new Participant(player2);
                List<Participant> participants = Arrays.asList(participant1, participant2);

                // Notify players
                MessagesLocale.MATCH_FOUND.send(uuid1,
                        new Replacement("<opponent>", participant2.getNameUnColored()),
                        new Replacement("<kit>", kit.getDisplayName()),
                        new Replacement("<arena>", arena.getDisplayName()),
                        new Replacement("<opponent-ping>", String.valueOf(PlayerUtil.getPing(uuid2))),
                        new Replacement("<ping>", String.valueOf(PlayerUtil.getPing(uuid1))));

                MessagesLocale.MATCH_FOUND.send(uuid2,
                        new Replacement("<opponent>", participant1.getNameUnColored()),
                        new Replacement("<kit>", kit.getDisplayName()),
                        new Replacement("<arena>", arena.getDisplayName()),
                        new Replacement("<opponent-ping>", String.valueOf(PlayerUtil.getPing(uuid1))),
                        new Replacement("<ping>", String.valueOf(PlayerUtil.getPing(uuid2))));

                MatchService.get().startMatch(participants, kit, arena, false,
                        kit.is(KitRule.BEST_OF_THREE) ? 3 : 1);
            }
        }
    }
}
