package dev.lrxh.neptune.providers.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.menus.kitEditor.KitEditorMenu;
import dev.lrxh.neptune.providers.menus.stats.StatsMenu;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public enum ItemAction {

    UNRANKED() {
        @Override
        public void execute(Player player) {
            new QueueMenu().openMenu(player);
        }
    },
    QUEUE_LEAVE() {
        @Override
        public void execute(Player player) {
            Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).setState(ProfileState.LOBBY);
            Neptune.get().getQueueManager().remove(player.getUniqueId());
            MessagesLocale.QUEUE_LEAVE.send(player.getUniqueId());
        }
    },
    KIT_EDITOR() {
        @Override
        public void execute(Player player) {
            new KitEditorMenu().openMenu(player);
        }
    },
    STATS() {
        @Override
        public void execute(Player player) {
            new StatsMenu(player).openMenu(player);
        }
    },
    SPEC_LEAVE(){
        @Override
        public void execute(Player player){
            Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getMatch().removeSpectator(player.getUniqueId());
        }
    };

    public abstract void execute(Player player);
}
