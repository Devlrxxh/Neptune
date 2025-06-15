package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.game.divisions.DivisionService;
import dev.lrxh.neptune.game.divisions.impl.Division;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KitData {
    private int wins = 0;
    private int losses = 0;
    private int bestStreak = 0;
    private int currentStreak = 0;
    private List<ItemStack> kitLoadout = new ArrayList<>();
    private Division division;

    public double getKdr() {
        if (losses == 0) return wins;
        double kd = (double) wins / losses;
        BigDecimal bd = new BigDecimal(kd);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void updateDivision() {
        division = DivisionService.get().getDivisionByWinCount(wins);
        if (division == null) {
            division = DivisionService.get().getDivisionByWinCount(0);
        }
    }
}

