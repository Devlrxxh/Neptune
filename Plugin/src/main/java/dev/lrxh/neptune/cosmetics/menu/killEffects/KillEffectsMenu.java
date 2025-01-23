package dev.lrxh.neptune.cosmetics.menu.killEffects;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.impl.KillEffect;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KillEffectsMenu extends Menu {
    public KillEffectsMenu() {
        super(MenusLocale.KILL_EFFECTS_TITLE.getString(), MenusLocale.KILL_EFFECTS_SIZE.getInt(), Filter.valueOf(MenusLocale.KILL_EFFECTS_FILTER.getString()), true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (KillEffect killEffect : KillEffect.values()) {
            buttons.add(new KillEffectButton(killEffect.getSlot(), killEffect));
        }

        return buttons;
    }
}
