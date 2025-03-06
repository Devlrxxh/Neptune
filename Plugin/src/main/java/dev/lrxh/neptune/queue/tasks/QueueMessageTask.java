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

public class QueueMessageTask extends NeptuneRunnable {
    @Override
    public void run() {
        for (QueueEntry queueEntry : QueueService.get().queue) {
            Profile profile = API.getProfile(queueEntry.getUuid());
            if (MessagesLocale.QUEUE_REPEAT_TOGGLE.getBoolean()) {
                MessagesLocale.QUEUE_REPEAT.send(queueEntry.getUuid(),
                        new Replacement("<kit>", queueEntry.getKit().getDisplayName()),
                        new Replacement("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing())));
            }
        }
    }
}
