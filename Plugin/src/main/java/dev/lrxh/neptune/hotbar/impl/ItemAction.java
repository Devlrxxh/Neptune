package dev.lrxh.neptune.hotbar.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.divisions.menu.DivisionsMenu;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.menu.editor.KitEditorMenu;
import dev.lrxh.neptune.kit.menu.stats.StatsMenu;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.menu.LeaderboardMenu;
import dev.lrxh.neptune.match.MatchManager;
import dev.lrxh.neptune.match.menu.MatchListMenu;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.menu.PartySettingsMenu;
import dev.lrxh.neptune.party.menu.buttons.events.PartyEventsMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.queue.QueueManager;
import dev.lrxh.neptune.queue.menu.QueueMenu;
import dev.lrxh.neptune.settings.menu.SettingsMenu;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public enum ItemAction {

    UNRANKED() {
        @Override
        public void execute(Player player) {
            new QueueMenu().openMenu(player.getUniqueId());
        }
    },
    QUEUE_LEAVE() {
        @Override
        public void execute(Player player) {
            API.getProfile(player.getUniqueId()).setState(ProfileState.IN_LOBBY);
            QueueManager.get().remove(player.getUniqueId());
            MessagesLocale.QUEUE_LEAVE.send(player.getUniqueId());
        }
    },
    KIT_EDITOR() {
        @Override
        public void execute(Player player) {
            new KitEditorMenu().openMenu(player.getUniqueId());
        }
    },
    STATS() {
        @Override
        public void execute(Player player) {
            new StatsMenu(player.getName()).openMenu(player.getUniqueId());
        }
    },
    SPECTATE_MENU {
        @Override
        public void execute(Player player) {
            if (MatchManager.get().matches.isEmpty()) {
                MessagesLocale.SPECTATE_MENU_NO_MATCH.send(player.getUniqueId());
                return;
            }

            new MatchListMenu().openMenu(player.getUniqueId());
        }
    },
    LEADERBOARDS() {
        @Override
        public void execute(Player player) {
            new LeaderboardMenu(LeaderboardType.WINS).openMenu(player.getUniqueId());
        }
    },
    PARTY_CREATE() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player.getUniqueId());
            profile.createParty();
        }
    },
    PARTY_INFO() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player.getUniqueId());
            if (profile.getGameData().getParty() == null) {
                MessagesLocale.PARTY_NOT_IN.send(player.getUniqueId());
                return;
            }
            Party party = profile.getGameData().getParty();
            MessagesLocale.PARTY_INFO.send(player.getUniqueId(),
                    new Replacement("<leader>", party.getLeaderName()),
                    new Replacement("<privacy>", party.isOpen() ? "Open" : "Closed"),
                    new Replacement("<max>", String.valueOf(party.getMaxUsers())),
                    new Replacement("<members>", party.getUserNames()),
                    new Replacement("<size>", String.valueOf(party.getUsers().size())));
        }
    },
    PARTY_DISBAND() {
        @Override
        public void execute(Player player) {
            API.getProfile(player.getUniqueId()).disband();
        }
    },
    PARTY_EVENTS() {
        @Override
        public void execute(Player player) {
            Party party = API.getProfile(player.getUniqueId()).getGameData().getParty();
            if (!party.getLeader().equals(player.getUniqueId())) {
                MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
                return;
            }
            if (party.getUsers().size() < 2) {
                MessagesLocale.PARTY_NOT_ENOUGH_MEMBERS.send(player.getUniqueId());
                return;
            }
            new PartyEventsMenu(API.getProfile(player.getUniqueId()).getGameData().getParty()).openMenu(player.getUniqueId());
        }
    },
    PARTY_SETTINGS() {
        @Override
        public void execute(Player player) {
            Party party = API.getProfile(player.getUniqueId()).getGameData().getParty();
            if (!party.getLeader().equals(player.getUniqueId())) {
                MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
                return;
            }
            new PartySettingsMenu(party).openMenu(player.getUniqueId());
        }
    },
    DIVISIONS() {
        @Override
        public void execute(Player player) {
            new DivisionsMenu().openMenu(player.getUniqueId());
        }
    },
    PLAY_AGAIN() {
        @Override
        public void execute(Player player) {
            final Neptune plugin = Neptune.get();
            Kit kit = KitManager.get().getKitByName(API.getProfile(player).getGameData().getLastKit());
            if (kit != null) {
                QueueManager.get().add(player.getUniqueId(), new Queue(kit));
            }
        }
    },
    SETTINGS() {
        @Override
        public void execute(Player player) {
            new SettingsMenu().openMenu(player.getUniqueId());
        }
    };

    public abstract void execute(Player player);
}
