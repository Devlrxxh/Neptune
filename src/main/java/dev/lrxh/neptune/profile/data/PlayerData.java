package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.WeakHashMap;

@Getter
@Setter
public class PlayerData {
    private WeakHashMap<Kit, KitData> kitData;
    private DuelRequest duelRequest;

    public PlayerData() {
        this.kitData = new WeakHashMap<>();
    }

    public void run(Kit kit, boolean ranked, boolean won) {
        if (won) {
            addWin(kit, ranked);
        } else {
            addLoss(kit, ranked);
            if (ranked) {
                kitData.get(kit).setRankedBestStreak(kitData.get(kit).getCurrentRankedStreak());
                kitData.get(kit).setCurrentRankedStreak(0);
            } else {
                kitData.get(kit).setUnrankedBestStreak(kitData.get(kit).getCurrentUnrankedStreak());
                kitData.get(kit).setCurrentUnrankedStreak(0);
            }
        }
    }

    public void addWin(Kit kit, boolean ranked) {
        if (ranked) {
            kitData.get(kit).setRankedWins(kitData.get(kit).getRankedWins() + 1);
        } else {
            kitData.get(kit).setUnrankedWins(kitData.get(kit).getUnrankedWins() + 1);
        }
        addWinStreak(kit, ranked);
    }

    public void addLoss(Kit kit, boolean ranked) {
        if (ranked) {
            kitData.get(kit).setRankedLosses(kitData.get(kit).getRankedLosses() + 1);
        } else {
            kitData.get(kit).setUnrankedLosses(kitData.get(kit).getUnrankedLosses() + 1);
        }
    }

    public void addWinStreak(Kit kit, boolean ranked) {
        if (ranked) {
            kitData.get(kit).setCurrentRankedStreak(kitData.get(kit).getCurrentRankedStreak() + 1);

            if (kitData.get(kit).getCurrentRankedStreak() > kitData.get(kit).getRankedBestStreak()) {
                setBestWinStreak(kit, kitData.get(kit).getCurrentRankedStreak(), true);
            }

        } else {
            kitData.get(kit).setCurrentUnrankedStreak(kitData.get(kit).getCurrentUnrankedStreak() + 1);

            if (kitData.get(kit).getCurrentUnrankedStreak() > kitData.get(kit).getUnrankedBestStreak()) {
                setBestWinStreak(kit, kitData.get(kit).getCurrentUnrankedStreak(), false);
            }
        }

    }

    public void setBestWinStreak(Kit kit, int value, boolean ranked) {
        if (ranked) {
            kitData.get(kit).setRankedBestStreak(value);
        } else {
            kitData.get(kit).setUnrankedBestStreak(value);
        }
    }

    public void addElo(Kit kit, int elo) {
        kitData.get(kit).setElo(kitData.get(kit).getElo() + elo);
    }
}
