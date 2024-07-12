package dev.lrxh.neptune.cosmetics.menu.killEffects;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.impl.KillEffect;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KillEffectsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return MenusLocale.KILL_EFFECTS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.KILL_EFFECTS_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.KILL_EFFECTS_FILTER.getString());
    }

    @Override
    public boolean isUpdateOnClick() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (KillEffect killEffect : KillEffect.values()) {
            buttons.put(killEffect.getSlot(), new KillEffectButton(killEffect));
        }

        return buttons;
    }
}
