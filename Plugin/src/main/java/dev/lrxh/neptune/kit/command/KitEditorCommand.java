package dev.lrxh.neptune.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.menu.editor.KitEditorMenu;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("kiteditor")
@Description("Kit Editor command")
public class KitEditorCommand extends BaseCommand {
    

    @Default
    public void open(Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        if (profile.hasState(ProfileState.IN_LOBBY, ProfileState.IN_PARTY)) {
            new KitEditorMenu().openMenu(player.getUniqueId());
        }
    }

    @Subcommand("reset")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void reset(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        Kit kit = KitManager.get().getKitByName(kitName);

        profile.getGameData().getKitData().get(kit).setKitLoadout(kit.getItems());

        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            profile.getGameData().getKitData().get(profile.getGameData().getKitEditor()).setKitLoadout
                    (Arrays.asList(player.getInventory().getContents()));

            MessagesLocale.KIT_EDITOR_STOP.send(player.getUniqueId());
            profile.setState(ProfileState.IN_LOBBY);
        }

        MessagesLocale.KIT_EDITOR_RESET.send(player.getUniqueId(), new Replacement("<kit>", kit.getDisplayName()));
    }

    private boolean checkKit(String kitName) {
        return KitManager.get().getKitByName(kitName) != null;
    }
}
