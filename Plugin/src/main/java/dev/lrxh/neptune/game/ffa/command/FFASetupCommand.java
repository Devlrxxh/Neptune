package dev.lrxh.neptune.game.ffa.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.game.ffa.FFAArena;
import dev.lrxh.neptune.game.ffa.FFAService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;

public class FFASetupCommand {

    @Command(name = "create", desc = "")
    @Require("neptune.admin")
    public void create(@Sender Player player, String arena) {
        FFAService.get().createArena(arena);
        player.sendMessage(CC.success("Arena created") );
    }

    @Command(name = "add", desc = "")
    @Require("neptune.admin")
    public void add(@Sender Player player, FFAArena ffaArena, Kit kit) {
        FFAService.get().addArenaAllowedKit(ffaArena, kit);
        FFAService.get().getKitArenas().put(kit, ffaArena);
        player.sendMessage(CC.success("Kit added to arena whitelist"));
    }

    @Command(name = "set", desc = "")
    @Require("neptune.admin")
    public void set(@Sender Player player, FFAArena arena, String location) {
        if (arena == null) {
            player.sendMessage(CC.error("Arena not found") );
            return;
        }
        arena.getSpawnLocations().put(location, player.getLocation());
    }

    @Command(name = "remove", desc = "")
    @Require("neptune.admin")
    public void remove(@Sender Player player, FFAArena arena, String location) {
        if (arena == null) {
            player.sendMessage(CC.error("Arena not found") );
            return;
        }
        arena.getSpawnLocations().remove(location);
    }

    @Command(name = "delete", desc = "")
    @Require("neptune.admin")
    public void delete(@Sender Player player, FFAArena arena) {
        if (arena == null) {
            player.sendMessage(CC.error("Arena not found") );
            return;
        }
        FFAService.get().getArenas().remove(arena);
    }
}
