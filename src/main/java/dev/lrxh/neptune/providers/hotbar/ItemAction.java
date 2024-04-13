package dev.lrxh.neptune.providers.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import org.bukkit.entity.Player;

public enum ItemAction {

    UNRANKED() {
        @Override
        public void execute(Player player) {
            new QueueMenu(false).openMenu(player);
        }
    },
    RANKED() {
        @Override
        public void execute(Player player) {
            new QueueMenu(true).openMenu(player);
        }
    },
    QUEUE_LEAVE() {
        @Override
        public void execute(Player player) {
            Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).setState(ProfileState.LOBBY);
            Neptune.get().getQueueManager().remove(player.getUniqueId());
            MessagesLocale.QUEUE_LEAVE.send(player.getUniqueId());
        }
    };

    public abstract void execute(Player player);
}
