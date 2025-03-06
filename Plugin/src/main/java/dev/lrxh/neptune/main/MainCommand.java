package dev.lrxh.neptune.main;

import com.github.retrooper.packetevents.util.Vector3d;
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.cosmetics.CosmeticService;
import dev.lrxh.neptune.hotbar.HotbarService;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.MatchService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;

public class MainCommand {

    @Command(name = "", desc = "")
    @Require("neptune.admin")
    public void help(@Sender Player player) {
        new MainMenu().open(player);
    }

    @Command(name = "setspawn", desc = "")
    @Require("neptune.admin")
    public void setspawn(@Sender Player player) {
        Neptune.get().getCache().setSpawn(player.getLocation());
        player.sendMessage(CC.color("&aSuccessfully set spawn!"));
    }

    @Command(name = "generate", desc = "", usage = "<arena> <amount>")
    public void generate(@Sender Player player, StandAloneArena arena, int amount) {
        arena.generateCopies(amount);
        player.sendMessage(CC.success("Generating arenas... &7Check console for more info"));
    }

    @Command(name = "reload", desc = "")
    @Require("neptune.admin")
    public void reload(@Sender Player player) {
        ConfigService.get().load();
        CosmeticService.get().load();
        HotbarService.get().loadItems();

        for (Player p : Bukkit.getOnlinePlayers()) {
            Profile profile = API.getProfile(player);
            if (profile.getState().equals(ProfileState.IN_GAME) || profile.getState().equals(ProfileState.IN_KIT_EDITOR))
                return;
            HotbarService.get().giveItems(p);
        }

        player.sendMessage(CC.color("&aSuccessfully reloaded configs!"));
    }

    @Command(name = "stop", desc = "")
    public void generate(@Sender Player player) {
        Neptune.get().setAllowMatches(false);

        for (Match match : MatchService.get().matches) {
            match.resetArena();
        }
    }
}