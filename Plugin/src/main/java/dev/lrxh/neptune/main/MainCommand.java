package dev.lrxh.neptune.main;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.cosmetics.CosmeticService;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;


public class MainCommand {

    @Command(name = "", desc = "")
    public void help(@Sender Player player) {
        new MainMenu().open(player);
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