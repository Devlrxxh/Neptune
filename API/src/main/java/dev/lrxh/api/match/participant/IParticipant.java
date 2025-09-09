package dev.lrxh.api.match.participant;

import java.util.UUID;

import org.bukkit.Location;

import dev.lrxh.api.utils.ITime;
import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.profile.IProfile;

public interface IParticipant {
    boolean isDead();
    UUID getPlayerUUID();
    String getName();
    String getNameColored();
    IParticipant getOpponent();
    IDeathCause getDeathCause();
    IParticipantColor getColor();
    IParticipant getLastAttacker();
    int getHits();
    int getLongestCombo();
    int getCombo();
    boolean isLoser();
    boolean isDisconnected();
    boolean isLeft();
    int getPoints();
    boolean isFrozen();
    boolean isBedBroken();
    ITime getTime();
    int getEloChange();
    Location getCurrentCheckPoint();
    int getCheckPoint();
    Location getSpawn(IMatch match);
    IProfile getProfile();
    String getHitsDifference(IParticipant participant);
    String getHitsDifferenceUncolored(IParticipant otherParticipant);

    void reset();
    void toggleFreeze();
    void addWin();
    void playKillEffect();
    void resetCombo();

}
