package dev.lrxh.neptune.kit.menu.button;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.kit.menu.KitRulesMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitRuleButton extends Button {
    private final KitRule kitRule;
    private final Kit kit;

    public KitRuleButton(int slot, Kit kit, KitRule kitRule) {
        super(slot, false);
        this.kitRule = kitRule;
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.toggle(kitRule);
        KitService.get().saveKits();
        new KitRulesMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return kit.is(kitRule) ? new ItemBuilder(kitRule.getIcon()).name("&a" + kitRule.getName()).lore("&7" + kitRule.getDescription()).clearFlags().build()
                : new ItemBuilder(kitRule.getIcon()).name("&c" + kitRule.getName()).lore("&7" + kitRule.getDescription()).clearFlags().build();
    }
}
