package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.arena.Arena;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Kit {
    private String name;
    private String displayName;
    private boolean ranked;
    private List<ItemStack> items;
    private List<ItemStack> armour;
    private HashSet<Arena> arenas;
    //RULES
    private boolean build;
}

