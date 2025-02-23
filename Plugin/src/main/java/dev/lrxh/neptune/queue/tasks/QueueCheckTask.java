package dev.lrxh.neptune.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.MatchService;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.QueueService;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QueueCheckTask extends NeptuneRunnable {
    @Override
    public void run() {
        if (QueueService.get().queues.isEmpty()) return;

        for (Map.Entry<UUID, Queue> entry1 : QueueService.get().queues.entrySet()) {
            //Check if 2 same queue were found in the queue
            UUID uuid1 = entry1.getKey();
            Profile profile1 = API.getProfile(uuid1);
            Queue queue1 = entry1.getValue();
            if (queue1.isArenaLoading()) continue;

            if (MessagesLocale.QUEUE_REPEAT_TOGGLE.getBoolean()) {
                MessagesLocale.QUEUE_REPEAT.send(uuid1,
                        new Replacement("<kit>", queue1.getKit().getDisplayName()),
                        new Replacement("<maxPing>", String.valueOf(profile1.getSettingData().getMaxPing())));
            }


            for (Map.Entry<UUID, Queue> entry2 : QueueService.get().queues.entrySet()) {
                UUID uuid2 = entry2.getKey();
                Queue queue2 = entry2.getValue();
                if (queue2.isArenaLoading()) continue;

                if ((uuid1.equals(uuid2))) continue;
                if (!QueueService.get().compare(queue1, queue2)) continue;
                SettingData settings1 = profile1.getSettingData();
                SettingData settings2 = API.getProfile(uuid2).getSettingData();

                if (!(PlayerUtil.getPing(uuid2) <= settings1.getMaxPing() &&
                        PlayerUtil.getPing(uuid1) <= settings2.getMaxPing())) {
                    continue;
                }

                Player player1 = Bukkit.getPlayer(uuid1);
                Player player2 = Bukkit.getPlayer(uuid2);

                if (player1 == null || player2 == null) continue;

                //Create participants
                Participant participant1 =
                        new Participant(player1);

                Participant participant2 =
                        new Participant(player2);

                List<Participant> participants = Arrays.asList(participant1, participant2);

                Arena arena = queue1.getKit().getRandomArena();

                //If no arenas were found
                if (arena == null) {

                    removeFromQueue(uuid1);
                    removeFromQueue(uuid2);

                    PlayerUtil.sendMessage(uuid1, CC.error("No arena was found!"));
                    PlayerUtil.sendMessage(uuid2, CC.error("No arena was found!"));
                    continue;
                }

                //If arena locations weren't setup
                if (!arena.isSetup()) {

                    removeFromQueue(uuid1);
                    removeFromQueue(uuid2);

                    PlayerUtil.sendMessage(uuid1, CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                    PlayerUtil.sendMessage(uuid2, CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                    continue;
                }

                //Send match found message
                MessagesLocale.MATCH_FOUND.send(uuid1,
                        new Replacement("<opponent>", participant2.getNameUnColored()),
                        new Replacement("<kit>", queue1.getKit().getDisplayName()),
                        new Replacement("<arena>", arena.getDisplayName()),
                        new Replacement("<opponent-ping>", String.valueOf(PlayerUtil.getPing(uuid2))),
                        new Replacement("<ping>", String.valueOf(PlayerUtil.getPing(uuid1))));

                MessagesLocale.MATCH_FOUND.send(uuid2,
                        new Replacement("<opponent>", participant1.getNameUnColored()),
                        new Replacement("<kit>", queue1.getKit().getDisplayName()),
                        new Replacement("<arena>", arena.getDisplayName()),
                        new Replacement("<opponent-ping>", String.valueOf(PlayerUtil.getPing(uuid1))),
                        new Replacement("<ping>", String.valueOf(PlayerUtil.getPing(uuid2))));

                if (arena instanceof StandAloneArena) {
                    queue1.setArenaLoading(true);
                    queue2.setArenaLoading(true);
                }

                //Start match
                MatchService.get().startMatch(participants, queue1.getKit(),
                        arena, false, queue1.getKit().is(KitRule.BEST_OF_THREE) ? 3 : 1);
            }
        }
    }

    private void removeFromQueue(UUID uuid) {
        QueueService.get().remove(uuid);
        API.getProfile(uuid).setState(ProfileState.IN_LOBBY);
    }
}
