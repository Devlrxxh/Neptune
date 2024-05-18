package dev.lrxh.neptune.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.ArenaType;
import dev.lrxh.neptune.arena.impl.EdgeType;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.menu.ArenaManagmentMenu;
import dev.lrxh.neptune.match.impl.ParticipantColor;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;

import java.util.HashSet;

@CommandAlias("arena")
@CommandPermission("neptune.admin.arena")
@Description("Command to manage and create new Arena.")
public class ArenaCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Subcommand("list")
    public void list(Player player) {
        if (player == null)
            return;

        if (plugin.getArenaManager().arenas.isEmpty()) {
            player.sendMessage(CC.error("No arenas found!"));
            return;
        }

        player.sendMessage(CC.color("&7&m----------------------------------"));
        player.sendMessage(CC.color("&9Arenas: "));
        player.sendMessage(" ");
        for (Arena arena : plugin.getArenaManager().arenas) {
            if (arena instanceof StandAloneArena && ((StandAloneArena) arena).isDuplicate()) continue;
            player.sendMessage(CC.color("&7- &9" + arena.getName() + " &7| " + arena.getDisplayName() + " &7| " + (arena.isEnabled() ? "&aEnabled" : "&cDisabled")));
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
            arena = StandAloneArena.builder()
                    .name(arenaName)
                    .displayName(arenaName)
                    .copies(new HashSet<>())
                    .enabled(true)
                    .build();
        } else {
            arena = SharedArena.builder()
                    .name(arenaName)
                    .displayName(arenaName)
                    .enabled(true)
                    .build();
        }

        plugin.getArenaManager().arenas.add(arena);
        plugin.getArenaManager().saveArenas();
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
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        if (arenaSpawn.equals(ParticipantColor.BLUE)) {
            arena.setBlueSpawn(player.getLocation());
            player.sendMessage(CC.color("&aSuccessfully set &9Blue &aspawn for arena " + arena.getDisplayName() + "&a!"));
        } else {
            arena.setRedSpawn(player.getLocation());
            player.sendMessage(CC.color("&aSuccessfully set &cRed &aspawn for arena " + arena.getDisplayName() + "&a!"));
        }
        plugin.getArenaManager().saveArenas();
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
        player.teleport(Neptune.get().getArenaManager().getArenaByName(arenaName).getRedSpawn());
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
        if (!(plugin.getArenaManager().getArenaByName(arenaName) instanceof StandAloneArena)) {
            player.sendMessage(CC.error("Arena isn't standalone!"));
            return;
        }
        StandAloneArena arena = (StandAloneArena) plugin.getArenaManager().getArenaByName(arenaName);

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
        plugin.getArenaManager().saveArenas();
    }

    @Subcommand("manage")
    @Syntax("<arena>")
    public void manage(Player player) {
        if (player == null) return;

        if (plugin.getArenaManager().arenas.isEmpty()) {
            player.sendMessage(CC.error("No arenas found!"));
            return;
        }

        new ArenaManagmentMenu().openMenu(player);
    }

    @Subcommand("deathY")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void deathY(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (!(arena instanceof StandAloneArena)) {
            player.sendMessage(CC.error("Arena must be standalone!"));
            return;
        }

        ((StandAloneArena) arena).setDeathY(player.getLocation().getY());
        player.sendMessage(CC.color("&aSuccessfully set Death Y for arena " + arena.getDisplayName()));

        plugin.getArenaManager().saveArenas();
    }

    @Subcommand("displayName")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void displayName(Player player, String arenaName, String displayName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);

        arena.setDisplayName(displayName);

        player.sendMessage(CC.color("&aSuccessfully set Display Name arena " + arena.getDisplayName()));

        plugin.getArenaManager().saveArenas();
    }

    @Subcommand("limit")
    @Syntax("<arena>")
    @CommandCompletion("@arenas")
    public void limit(Player player, String arenaName) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);

        if (!(arena instanceof StandAloneArena)) {
            player.sendMessage(CC.error("Arena must be standalone!"));
            return;
        }

        ((StandAloneArena) arena).setLimit(player.getLocation().getY());
        player.sendMessage(CC.color("&aSuccessfully set Build limit for arena " + arena.getDisplayName()));

        plugin.getArenaManager().saveArenas();
    }

    private boolean checkArena(String arenaName) {
        return plugin.getArenaManager().getArenaByName(arenaName) != null;
    }
}