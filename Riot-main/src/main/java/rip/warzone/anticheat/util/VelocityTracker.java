package rip.warzone.anticheat.util;

import lombok.Getter;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityVelocity;

import java.util.Deque;
import java.util.LinkedList;

@Getter
public class VelocityTracker {

    private final Deque<Velocity> velocities=new LinkedList<>();

    public void handleFlying(final PacketPlayInFlying packet) {
        velocities.removeIf(velocity -> {
            velocity.onMove();
            return velocity.hasExpired();
        });
    }

    public void handleVelocity(final PacketPlayOutEntityVelocity packet) {
        /*final EntityVelocityWrapper entityVelocityWrapper = new EntityVelocityWrapper(packet);

        if(entityVelocityWrapper.getId() == playerData.getPlayer().getEntityId()) {
            double velocityX = Math.abs(entityVelocityWrapper.getX() / 8000D);
            double velocityY = entityVelocityWrapper.getY() / 8000D;
            double velocityZ = Math.abs(entityVelocityWrapper.getZ() / 8000D);

            velocities.add(new Velocity(velocityX, velocityY, velocityZ));
        }*/
    }

    @Getter
    public class Velocity {
        private final double x, y, z;
        private final double horizontal;

        private int ticksExisted;

        public Velocity(final double x, final double y, final double z) {
            this.x=x;
            this.y=y;
            this.z=z;

            horizontal=Math.hypot(x, z);

            ticksExisted=(int) (((x + z) / 2D + 2D) * 15D);
        }

        public void onMove() {
            --ticksExisted;
        }

        public boolean hasExpired() {
            return ticksExisted < 0;
        }
    }

}
