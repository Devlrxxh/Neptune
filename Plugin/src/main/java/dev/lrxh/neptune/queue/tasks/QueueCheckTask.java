package dev.lrxh.neptune.queue.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QueueCheckTask extends NeptuneRunnable {
    private final Neptune plugin;

    public QueueCheckTask() {
        this.plugin = Neptune.get();
    }

    @Override
    public void run() {
        if (plugin.getQueueManager().queues.isEmpty()) return;

        for (Map.Entry<UUID, Queue> entry1 : plugin.getQueueManager().queues.entrySet()) {

            //Check if 2 same queue were found in the queue
            UUID uuid1 = entry1.getKey();
            Queue queue1 = entry1.getValue();

            for (Map.Entry<UUID, Queue> entry2 : plugin.getQueueManager().queues.entrySet()) {
                UUID uuid2 = entry2.getKey();
                Queue queue2 = entry2.getValue();

                if ((uuid1.equals(uuid2))) return;
                if (!plugin.getQueueManager().compareQueue(queue1, queue2)) return;

                //Create participants
                Participant participant1 =
                        new Participant(uuid1);

                Participant participant2 =
                        new Participant(uuid2);

                List<Participant> participants = Arrays.asList(participant1, participant2);

                Arena arena = queue1.getKit().getRandomArena();

                //If no arenas were found
                if (arena == null) {

                    removeFromQueue(uuid1);
                    removeFromQueue(uuid2);

                    PlayerUtil.sendMessage(uuid1, CC.error("No arena was found!"));
                    PlayerUtil.sendMessage(uuid2, CC.error("No arena was found!"));
                    return;
                }

                //If arena locations weren't setup
                if (arena.getBlueSpawn() == null ||
                        arena.getRedSpawn() == null ||
                        (arena instanceof StandAloneArena &&
                                (((StandAloneArena) arena).getMax() == null ||
                                        ((StandAloneArena) arena).getMin() == null))) {

                    removeFromQueue(uuid1);
                    removeFromQueue(uuid2);

                    PlayerUtil.sendMessage(uuid1, CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                    PlayerUtil.sendMessage(uuid2, CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                    return;
                }

                //Set arena as being used
                if (arena instanceof StandAloneArena) {
                    ((StandAloneArena) arena).setUsed(true);
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

                //Start match
                plugin.getMatchManager().startMatch(participants, queue1.getKit(),
                        arena, false, queue1.getKit().is(KitRule.BEST_OF_THREE) ? 3 : 1);

                //Remove the players from queue
                plugin.getQueueManager().remove(uuid1);
                plugin.getQueueManager().remove(uuid2);

            }
        }
    }

    private void removeFromQueue(UUID uuid) {
        plugin.getQueueManager().remove(uuid);
        plugin.getProfileManager().getByUUID(uuid).setState(ProfileState.IN_LOBBY);
    }
}
