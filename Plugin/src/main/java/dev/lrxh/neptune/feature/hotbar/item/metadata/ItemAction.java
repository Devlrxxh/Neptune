package dev.lrxh.neptune.feature.hotbar.item.metadata;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.divisions.menu.DivisionsMenu;
import dev.lrxh.neptune.feature.leaderboard.metadata.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.menu.LeaderboardMenu;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.menu.PartySettingsMenu;
import dev.lrxh.neptune.feature.party.menu.buttons.events.PartyDuelMenu;
import dev.lrxh.neptune.feature.party.menu.buttons.events.PartyEventsMenu;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.feature.queue.menu.QueueMenu;
import dev.lrxh.neptune.feature.settings.menu.SettingsMenu;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.kit.menu.StatsMenu;
import dev.lrxh.neptune.game.kit.menu.editor.KitEditorMenu;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.menu.MatchListMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

@SuppressWarnings("unused")
public enum ItemAction {

    UNRANKED() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);

            if (profile.getState().equals(ProfileState.IN_PARTY)) {
                player.sendMessage(CC.color("&cYou can't queue while in a party."));
                return;
            }
            new QueueMenu().open(player);
        }
    },

    QUEUE_LEAVE() {
        @Override
        public void execute(Player player) {
            API.getProfile(player.getUniqueId()).setState(ProfileState.IN_LOBBY);
            QueueService.get().remove(player.getUniqueId());
            MessagesLocale.QUEUE_LEAVE.send(player.getUniqueId());
        }
    },

    KIT_EDITOR() {
        @Override
        public void execute(Player player) {
            new KitEditorMenu().open(player);
        }
    },

    STATS() {
        @Override
        public void execute(Player player) {
            new StatsMenu(player.getName()).open(player);
        }
    },

    SPECTATE_MENU {
        @Override
        public void execute(Player player) {
            new MatchListMenu().open(player);
        }
    },

    LEADERBOARDS() {
        @Override
        public void execute(Player player) {
            new LeaderboardMenu(LeaderboardType.KILLS).open(player);
        }
    },

    PARTY_CREATE() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);
            profile.createParty();
        }
    },

    PARTY_INFO() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);
            if (profile.getGameData().getParty() == null) {
                MessagesLocale.PARTY_NOT_IN.send(player);
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

            new PartyEventsMenu(API.getProfile(player.getUniqueId()).getGameData().getParty()).open(player);
        }
    },

    PARTY_DUEL() {
        @Override
        public void execute(Player player) {
            Party party = API.getProfile(player.getUniqueId()).getGameData().getParty();
            if (!party.getLeader().equals(player.getUniqueId())) {
                MessagesLocale.PARTY_NO_PERMISSION.send(player.getUniqueId());
                return;
            }
            new PartyDuelMenu(API.getProfile(player.getUniqueId()).getGameData().getParty()).open(player);
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
            new PartySettingsMenu(party).open(player);
        }
    },

    DIVISIONS() {
        @Override
        public void execute(Player player) {
            new DivisionsMenu().open(player);
        }
    },

    PLAY_AGAIN() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);

            Match match = profile.getMatch();
            if (match == null) return;

            Optional<Participant> participant = match.getParticipant(player);
            participant.orElseThrow().setDisconnected(true);
            PlayerUtil.reset(participant.orElseThrow().getPlayer());
            PlayerUtil.teleportToSpawn(participant.orElseThrow().getPlayerUUID());

            profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
            profile.setMatch(null);

            QueueService.get().add(new QueueEntry(match.getKit(), player.getUniqueId()), true);
        }
    },

    REMATCH() {
        @Override
        public void execute(Player player) {
            Profile profile = API.getProfile(player);
            if (profile == null) return;
            SoloFightMatch match = (SoloFightMatch) profile.getMatch();
            if (match == null) return;

            match.getKit().getRandomArena().thenAccept(arena -> {
                if (arena == null) {
                    player.sendMessage(CC.error("No arenas were found!"));
                    return;
                }

                DuelRequest duelRequest = new DuelRequest(profile.getPlayerUUID(), match.getKit(), arena, false, match.getRounds());
                Player opponent = match.getParticipant(player).orElseThrow().getOpponent().getPlayer();
                if (opponent == null) return;

                Profile opponentProfile = API.getProfile(opponent);
                if (opponentProfile == null) return;

                opponentProfile.sendRematch(duelRequest);
            });
        }
    },

    SETTINGS() {
        @Override
        public void execute(Player player) {
            new SettingsMenu().open(player);
        }
    };

    /**
     * Executes the action for the given player.
     *
     * @param player the player executing this action
     */
    public abstract void execute(Player player);
}
