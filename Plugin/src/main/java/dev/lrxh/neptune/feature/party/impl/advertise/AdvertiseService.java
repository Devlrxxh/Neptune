package dev.lrxh.neptune.feature.party.impl.advertise;

import java.util.HashMap;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.impl.AdvertiseRunnable;

public class AdvertiseService {
    private static HashMap<Party, AdvertiseRunnable> advertisedParties = new HashMap<>();
    public static void add(Party party) {
        AdvertiseRunnable runnable = new AdvertiseRunnable(party);
        runnable.start(SettingsLocale.PARTY_ADVERTISE_TIME.getInt() / 2, SettingsLocale.PARTY_ADVERTISE_TIME.getInt());
        advertisedParties.put(party, runnable);
    }
    public static void remove(Party party) {
        AdvertiseRunnable runnable = advertisedParties.remove(party);
        if (runnable != null) runnable.stop();
    }
    public static boolean has(Party party) {
        return advertisedParties.containsKey(party);
    }
    public static boolean has(AdvertiseRunnable runnable) {
        return advertisedParties.containsValue(runnable);
    }
    public static AdvertiseRunnable getRunnable(Party party) {
        return advertisedParties.get(party);
    }
}
