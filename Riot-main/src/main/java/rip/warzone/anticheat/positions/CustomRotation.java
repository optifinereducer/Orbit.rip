package rip.warzone.anticheat.positions;

public class CustomRotation {
    private final float yaw;
    private final float pitch;

    public CustomRotation(float yaw, float pitch) {
        this.yaw=yaw;
        this.pitch=pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}

