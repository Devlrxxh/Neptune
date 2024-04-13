package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@UtilityClass
public class VisibilityLogic {

    public void handle(UUID playerUUID){
        for(Player players : Bukkit.getOnlinePlayers()){
            handle(playerUUID, players.getUniqueId());
        }
    }

    public void handle(UUID viewerUUID, UUID otherUUID){
        if(Bukkit.getPlayer(viewerUUID) == null) return;
        if(Bukkit.getPlayer(otherUUID) == null) return;
        Player viewerPlayer = Bukkit.getPlayer(viewerUUID);
        Player otherPlayer = Bukkit.getPlayer(otherUUID);

        Profile viewerProfile = Neptune.get().getProfileManager().getByUUID(viewerUUID);
        Profile otherProfile = Neptune.get().getProfileManager().getByUUID(otherUUID);

        if(viewerProfile.getMatch() != null && otherProfile.getMatch() != null && viewerProfile.getMatch().getUuid().equals(otherProfile.getMatch().getUuid())){
            viewerPlayer.showPlayer(otherPlayer);
            otherPlayer.showPlayer(viewerPlayer);
        }else{
            viewerPlayer.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(viewerPlayer);
        }
    }
}
