package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Participant;

import java.util.*;

public class QueueTask implements Runnable {
    private final Neptune plugin = Neptune.get();

    @Override
    public void run() {
        for (Map.Entry<UUID, Queue> entry1 : plugin.getQueueManager().queueMap.entrySet()) {

            //Check if 2 same queue were found in the queue
            UUID uuid1 = entry1.getKey();
            Queue queue1 = entry1.getValue();

            for (Map.Entry<UUID, Queue> entry2 : plugin.getQueueManager().queueMap.entrySet()) {
                UUID uuid2 = entry2.getKey();
                Queue queue2 = entry2.getValue();

                if ((!uuid1.equals(uuid2)) && queue1.equals(queue2)) {

                    //Start match
                    //TODO: THIS NEEDS IMPROVEMENTS
                    Participant participant1 =
                            new Participant(uuid1, null, false);

                    Participant participant2 =
                            new Participant(uuid2,
                                    new HashSet<>(Collections.singletonList(participant1)), false);

                    participant1.setOpponent(new HashSet<>(Collections.singletonList(participant2)));
                    //TODO: THIS NEEDS IMPROVEMENTS

                    HashSet<Participant> participants =
                            new HashSet<>(Arrays.asList(participant1, participant2));

                    plugin.getMatchManager().startMatch(participants, queue1.getKit(),
                            plugin.getArenaManager().getRandomArena(queue1.getKit()), queue1.isRanked(), false);

                    //Remove the players from queue
                    plugin.getQueueManager().queueMap.remove(uuid1);
                    plugin.getQueueManager().queueMap.remove(uuid2);
                }
            }
        }

    }
}
