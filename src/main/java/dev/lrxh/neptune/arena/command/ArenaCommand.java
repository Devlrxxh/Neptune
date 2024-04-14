package dev.lrxh.neptune.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.ArenaType;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.match.impl.ParticipantColor;
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

        if (plugin.getArenaManager().arenas.isEmpty()) {
            player.sendMessage(CC.error("No arenas found!"));
            return;
        }

        player.sendMessage(CC.translate("&7&m----------------------------------"));
        player.sendMessage(CC.translate("&9Arenas: "));
        player.sendMessage(" ");
        plugin.getArenaManager().arenas.forEach(arena -> player.sendMessage(CC.translate("&7- &9" + arena.getName() + " &7| " + arena.getDisplayName() + " &7| " + (arena.isEnabled() ? "&aEnabled" : "&cDisabled"))));
        player.sendMessage(CC.translate("&7&m----------------------------------"));
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
        player.sendMessage(CC.translate("&aSuccessfully created new Arena!"));
    }

    @Subcommand("setspawn")
    @Syntax("<arena> <red/blue>")
    @CommandCompletion("@arenas")
    public void setinv(Player player, String arenaName, ParticipantColor arenaSpawn) {
        if (player == null) return;
        if (!checkArena(arenaName)) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        if (arenaSpawn.equals(ParticipantColor.BLUE)) {
            arena.setBlueSpawn(player.getLocation());
            player.sendMessage(CC.translate("&aSuccessfully set &9Blue &aspawn for arena " + arena.getDisplayName() + "&a!"));
        } else {
            arena.setRedSpawn(player.getLocation());
            player.sendMessage(CC.translate("&aSuccessfully set &cRed &aspawn for arena " + arena.getDisplayName() + "&a!"));
        }
        plugin.getArenaManager().saveArenas();
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
            player.sendMessage(CC.error("Arena must be standalone to set deathY!"));
            return;
        }

        ((StandAloneArena) arena).setDeathY(player.getLocation().getY());

        plugin.getArenaManager().saveArenas();
    }

    private boolean checkArena(String arenaName) {
        return plugin.getArenaManager().getArenaByName(arenaName) != null;
    }
}