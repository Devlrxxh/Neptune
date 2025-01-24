package dev.lrxh.neptune.commands;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.cosmetics.CosmeticService;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;


public class MainCommand {

    @Command(name = "help", aliases = "", desc = "")
    public void help(@Sender Player player) {
        player.sendMessage(CC.color("&7&m-----------------------------------------"));
        player.sendMessage(CC.color("&9Neptune Core"));
        player.sendMessage(" ");
        player.sendMessage(CC.color("&7* &9/neptune setspawn &7- &fSet server spawn"));
        player.sendMessage(CC.color("&7* &9/neptune reload &7- &fReload all configs"));
        player.sendMessage(" ");
        player.sendMessage(CC.color("&7&m-----------------------------------------"));
    }

    @Command(name = "setspawn", desc = "")
    public void setspawn(@Sender Player player) {
        Neptune.get().getCache().setSpawn(player.getLocation());
        player.sendMessage(CC.color("&aSuccessfully set spawn!"));
    }


    @Command(name = "reload", desc = "")
    public void reload(@Sender Player player) {
        ConfigService.get().load();
        CosmeticService.get().load();
        player.sendMessage(CC.color("&aSuccessfully reloaded configs!"));
    }
}