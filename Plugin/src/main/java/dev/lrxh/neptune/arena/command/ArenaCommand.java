package dev.lrxh.neptune.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaManager;
import dev.lrxh.neptune.arena.impl.ArenaType;
import dev.lrxh.neptune.arena.impl.EdgeType;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.menu.ArenaManagmentMenu;
import dev.lrxh.neptune.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;

@CommandAlias("arena")
@CommandPermission("neptune.admin.arena")
@Description("Command to manage and create new Arena.")
public class ArenaCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Subcommand("list")
    public void list(Player player) {
        if (player == null)
            return;

        if (ArenaManager.get().arenas.isEmpty()) {
            player.sendMessage(CC.error("No arenas found!"));
            return;
        }

        player.sendMessage(CC.color("&7&m----------------------------------"));
        player.sendMessage(CC.color("&9Arenas: "));
        player.sendMessage(" ");
        for (Arena arena : ArenaManager.get().arenas) {
            player.sendMessage(CC.color("&7- &9" + arena.getName() + " &7| " + arena.getDisplayName() + " &7| " + (arena.isEnabled() ? "&aEnabled" : "&cDisabled") + " &7| " + (arena instanceof StandAloneArena ? "&8Standalone" : "&8Shared")));
        }
        player.sendMessage(CC.color("&7&m----------------------------------"));
    }

    @Subcommand("create")
    @Syntax("<name> <type>")
    public void create(Player player, String arenaName, ArenaType arenaType) {
        if (player == null) return;
        if (checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena already exists!"));
            return;
        }

        Arena arena;

        if (arenaType.equals(ArenaType.STANDALONE)) {
            arena = new StandAloneArena(arenaName);

        } else {
            arena = new SharedArena(arenaName);
        }

        ArenaManager.get().arenas.add(arena);
        ArenaManager.get().saveArenas();
        player.sendMessage(CC.color("&aSuccessfully created new Arena!"));
    }

    @Subcommand("setspawn")
    @Syntax("<arena> <red/blue>")
    @CommandCompletion("@arenas")
    public void setspawn(Player player, String arenaName, ParticipantColor arenaSpawn) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = ArenaManager.get().getArenaByName(arenaName);
        if (arenaSpawn.equals(ParticipantColor.BLUE)) {
            arena.setBlueSpawn(player.getLocation());
            player.sendMessage(CC.color("&aSuccessfully set &9Blue &aspawn for arena " + arena.getDisplayName() + "&a!"));
        } else {
            arena.setRedSpawn(player.getLocation());
            player.sendMessage(CC.color("&aSuccessfully set &cRed &aspawn for arena " + arena.getDisplayName() + "&a!"));
        }

        if (arena instanceof StandAloneArena && ((StandAloneArena) arena).getLimit() == 68321) {
            ((StandAloneArena) arena).setLimit(player.getLocation().getY() + 10);
        }

        ArenaManager.get().saveArenas();
    }

    @Subcommand("tp")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void tp(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = ArenaManager.get().getArenaByName(arenaName);
        if (arena.getRedSpawn() != null) {
            player.teleport(arena.getRedSpawn());
        } else if (arena.getBlueSpawn() != null) {
            player.teleport(arena.getBlueSpawn());
        } else {
            player.sendMessage(CC.error("Arena isn't setup completely, can't teleport."));
            return;
        }

        player.sendMessage(CC.color("&aThere you go!"));
    }

    @Subcommand("setedge")
    @Syntax("<arena> <min/max>")
    @CommandCompletion("@arenas")
    public void setEdge(Player player, String arenaName, EdgeType edgeType) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        if (!(ArenaManager.get().getArenaByName(arenaName) instanceof StandAloneArena arena)) {
            player.sendMessage(CC.error("Arena isn't standalone!"));
            return;
        }

        if (edgeType.equals(EdgeType.MIN)) {
            arena.setMin(player.getLocation());
            player.sendMessage(CC.color("&aSuccessfully set &9Min&a for arena " + arena.getDisplayName()));
        } else {
            arena.setMax(player.getLocation());
            player.sendMessage(CC.color("&aSuccessfully set &cMax&a for arena " + arena.getDisplayName()));
        }

        if (arena.getMin() != null && arena.getMax() != null) {
            arena.takeSnapshot();
        }
        ArenaManager.get().saveArenas();
    }

    @Subcommand("regenerate")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void regenerate(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        if (!(ArenaManager.get().getArenaByName(arenaName) instanceof StandAloneArena arena)) {
            player.sendMessage(CC.error("Arena isn't standalone!"));
            return;
        }

        arena.restoreSnapshot();

        player.sendMessage(CC.color("&aSuccessfully regenerated arena"));
    }

    @Subcommand("take")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void take(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        if (!(ArenaManager.get().getArenaByName(arenaName) instanceof StandAloneArena arena)) {
            player.sendMessage(CC.error("Arena isn't standalone!"));
            return;
        }

        arena.takeSnapshot();

        player.sendMessage(CC.color("&aSuccessfully captured arena"));
    }

    @Subcommand("manage")
    @Syntax("<arena>")
    public void manage(Player player) {
        if (player == null) return;

        if (ArenaManager.get().arenas.isEmpty()) {
            player.sendMessage(CC.error("No arenas found!"));
            return;
        }

        new ArenaManagmentMenu().openMenu(player.getUniqueId());
    }

    @Subcommand("setDeathY")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void setDeathY(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = ArenaManager.get().getArenaByName(arenaName);

        if (!(arena instanceof StandAloneArena)) {
            player.sendMessage(CC.error("Arena must be standalone!"));
            return;
        }

        ((StandAloneArena) arena).setDeathY(player.getLocation().getY());

        player.sendMessage(CC.color("&aSuccessfully set Death Y for arena " + arena.getDisplayName()));

        ArenaManager.get().saveArenas();
    }

    @Subcommand("delete")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void delete(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = ArenaManager.get().getArenaByName(arenaName);

        arena.delete();
        player.sendMessage(CC.color("&aSuccessfully delete arena " + arena.getDisplayName()));

        ArenaManager.get().saveArenas();
    }

    @Subcommand("setdisplayName")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void displayName(Player player, String arenaName, String displayName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = ArenaManager.get().getArenaByName(arenaName);

        arena.setDisplayName(displayName);

        player.sendMessage(CC.color("&aSuccessfully set Display Name arena " + arena.getDisplayName()));

        ArenaManager.get().saveArenas();
    }

    @Subcommand("setlimit")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void setlimit(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;

        }
        Arena arena = ArenaManager.get().getArenaByName(arenaName);

        if (!(arena instanceof StandAloneArena)) {
            player.sendMessage(CC.error("Arena must be standalone!"));
            return;
        }

        ((StandAloneArena) arena).setLimit(player.getLocation().getY());
        player.sendMessage(CC.color("&aSuccessfully set Build limit for arena " + arena.getDisplayName()));

        ArenaManager.get().saveArenas();
    }

    private boolean checkArena(String arenaName) {
        return ArenaManager.get().getArenaByName(arenaName) != null;
    }
}