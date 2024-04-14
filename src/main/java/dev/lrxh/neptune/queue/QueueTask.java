package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QueueTask extends BukkitRunnable {
    private final Neptune plugin = Neptune.get();

    @Override
    public void run() {
        if (!plugin.getQueueManager().queues.isEmpty()) {
            for (Map.Entry<UUID, Queue> entry1 : plugin.getQueueManager().queues.entrySet()) {

                //Check if 2 same queue were found in the queue
                UUID uuid1 = entry1.getKey();
                Queue queue1 = entry1.getValue();

                for (Map.Entry<UUID, Queue> entry2 : plugin.getQueueManager().queues.entrySet()) {
                    UUID uuid2 = entry2.getKey();
                    Queue queue2 = entry2.getValue();

                    if ((!uuid1.equals(uuid2))) {
                        if (plugin.getQueueManager().compareQueue(queue1, queue2)) {

                            //Create participants
                            Participant participant1 =
                                    new Participant(uuid1, Bukkit.getPlayer(uuid1).getDisplayName());

                            Participant participant2 =
                                    new Participant(uuid2, Bukkit.getPlayer(uuid2).getDisplayName());

                            List<Participant> participants = Arrays.asList(participant1, participant2);

                            Arena arena = plugin.getArenaManager().getRandomArena(queue1.getKit());

                            //If no arenas were found
                            if (arena == null) {
                                plugin.getQueueManager().remove(uuid1);
                                plugin.getProfileManager().getByUUID(uuid1).setState(ProfileState.IN_QUEUE);

                                plugin.getQueueManager().remove(uuid2);
                                plugin.getProfileManager().getByUUID(uuid2).setState(ProfileState.IN_QUEUE);

                                Bukkit.getPlayer(uuid1).sendMessage(CC.error("No arena was found!"));
                                Bukkit.getPlayer(uuid2).sendMessage(CC.error("No arena was found!"));
                                return;
                            }

                            //If arena locations weren't setup
                            if (arena.getBlueSpawn() == null ||
                                    arena.getRedSpawn() == null ||
                                    (arena instanceof StandAloneArena &&
                                            (((StandAloneArena) arena).getEdge2() == null ||
                                                    ((StandAloneArena) arena).getEdge1() == null))) {

                                plugin.getQueueManager().remove(uuid1);
                                plugin.getProfileManager().getByUUID(uuid1).setState(ProfileState.IN_QUEUE);

                                plugin.getQueueManager().remove(uuid2);
                                plugin.getProfileManager().getByUUID(uuid2).setState(ProfileState.IN_QUEUE);


                                Bukkit.getPlayer(uuid1).sendMessage(CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                                Bukkit.getPlayer(uuid2).sendMessage(CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
                                return;
                            }

                            //Send match found message
                            MessagesLocale.MATCH_FOUND.send(uuid1,
                                    new Replacement("<opponent>", participant2.getNameUnColored()),
                                    new Replacement("<kit>", queue1.getKit().getDisplayName()),
                                    new Replacement("<arena>", arena.getDisplayName()),
                                    new Replacement("<opponent-ping>", String.valueOf(PlayerUtils.getPing(uuid2))),
                                    new Replacement("<ping>", String.valueOf(PlayerUtils.getPing(uuid1))));

                            MessagesLocale.MATCH_FOUND.send(uuid2,
                                    new Replacement("<opponent>", participant1.getNameUnColored()),
                                    new Replacement("<kit>", queue1.getKit().getDisplayName()),
                                    new Replacement("<arena>", arena.getDisplayName()),
                                    new Replacement("<opponent-ping>", String.valueOf(PlayerUtils.getPing(uuid1))),
                                    new Replacement("<ping>", String.valueOf(PlayerUtils.getPing(uuid2))));

                            //Set arena as being used
                            if (arena instanceof StandAloneArena) {
                                ((StandAloneArena) arena).setUsed(true);
                            }

                            //Start match
                            plugin.getMatchManager().startMatch(participants, queue1.getKit(),
                                    arena, queue1.isRanked(), false);

                            //Remove the players from queue
                            plugin.getQueueManager().remove(uuid1);
                            plugin.getQueueManager().remove(uuid2);
                        }
                    }
                }
            }
        }
    }
}
