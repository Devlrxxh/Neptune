package dev.lrxh.neptune.profile.data;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchHistory {
    @SerializedName("won")
    private final boolean won;
    @SerializedName("opponent")
    private final String opponentName;
    @SerializedName("kit")
    private String kitName;
    @SerializedName("arena")
    private String arenaName;
}
