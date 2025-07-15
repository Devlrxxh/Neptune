package dev.lrxh.neptune.cache;

import com.github.retrooper.packetevents.util.Vector3d;

import java.util.UUID;

public class ShooterData {
    public UUID shooterId;
    public Vector3d pos;
    public long timestamp;

    public void set(UUID shooterId, Vector3d pos, long timestamp) {
        this.shooterId = shooterId;
        this.pos = pos;
        this.timestamp = timestamp;
    }
}
