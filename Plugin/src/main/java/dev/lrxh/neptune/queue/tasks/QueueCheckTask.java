package dev.lrxh.neptune.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.MatchService;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.queue.QueueEntry;
import dev.lrxh.neptune.queue.QueueService;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class QueueCheckTask extends NeptuneRunnable {
    @Override
    public void run() {
        if (QueueService.get().queue.size() < 2) return;

        QueueEntry queueEntry1 = QueueService.get().queue.poll();
        QueueEntry queueEntry2 = QueueService.get().queue.poll();

        if (queueEntry2 == null || queueEntry1 == null) return;

        UUID uuid1 = queueEntry1.getUuid();
        Profile profile1 = API.getProfile(uuid1);

        if (MessagesLocale.QUEUE_REPEAT_TOGGLE.getBoolean()) {
            MessagesLocale.QUEUE_REPEAT.send(uuid1,
                    new Replacement("<kit>", queueEntry1.getKit().getDisplayName()),
                    new Replacement("<maxPing>", String.valueOf(profile1.getSettingData().getMaxPing())));
        }

        UUID uuid2 = queueEntry2.getUuid();

        if (!QueueService.get().compare(queueEntry1, queueEntry2)) return;
        SettingData settings1 = profile1.getSettingData();
        SettingData settings2 = API.getProfile(uuid2).getSettingData();

        if (!(PlayerUtil.getPing(uuid2) <= settings1.getMaxPing() &&
                PlayerUtil.getPing(uuid1) <= settings2.getMaxPing())) {
            return;
        }

        Player player1 = Bukkit.getPlayer(uuid1);
        Player player2 = Bukkit.getPlayer(uuid2);

        if (player1 == null || player2 == null) return;

        Participant participant1 =
                new Participant(player1);

        Participant participant2 =
                new Participant(player2);

        List<Participant> participants = Arrays.asList(participant1, participant2);

        Arena arena = queueEntry1.getKit().getRandomArena();

        if (arena == null) {

            API.getProfile(uuid1).setState(ProfileState.IN_LOBBY);
            API.getProfile(uuid2).setState(ProfileState.IN_LOBBY);

            PlayerUtil.sendMessage(uuid1, CC.error("No arena was found!"));
            PlayerUtil.sendMessage(uuid2, CC.error("No arena was found!"));
            return;
        }

        if (!arena.isSetup()) {

            API.getProfile(uuid1).setState(ProfileState.IN_LOBBY);
            API.getProfile(uuid2).setState(ProfileState.IN_LOBBY);

            PlayerUtil.sendMessage(uuid1, CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
            PlayerUtil.sendMessage(uuid2, CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
            return;
        }

        //Send match found message
        MessagesLocale.MATCH_FOUND.send(uuid1,
                new Replacement("<opponent>", participant2.getNameUnColored()),
                new Replacement("<kit>", queueEntry1.getKit().getDisplayName()),
                new Replacement("<arena>", arena.getDisplayName()),
                new Replacement("<opponent-ping>", String.valueOf(PlayerUtil.getPing(uuid2))),
                new Replacement("<ping>", String.valueOf(PlayerUtil.getPing(uuid1))));

        MessagesLocale.MATCH_FOUND.send(uuid2,
                new Replacement("<opponent>", participant1.getNameUnColored()),
                new Replacement("<kit>", queueEntry1.getKit().getDisplayName()),
                new Replacement("<arena>", arena.getDisplayName()),
                new Replacement("<opponent-ping>", String.valueOf(PlayerUtil.getPing(uuid1))),
                new Replacement("<ping>", String.valueOf(PlayerUtil.getPing(uuid2))));

        MatchService.get().startMatch(participants, queueEntry1.getKit(),
                arena, false, queueEntry1.getKit().is(KitRule.BEST_OF_THREE) ? 3 : 1);
    }
}
