package dev.lrxh.neptune.profile.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
public class KitData {
    private int unrankedElo, rankedElo;
    private int unrankedWins, rankedWins;
    private int unrankedLosses, rankedLosses;
    private int unrankedStreak, rankedStreak;
    private int elo;
    private List<ItemStack> kit;
}
