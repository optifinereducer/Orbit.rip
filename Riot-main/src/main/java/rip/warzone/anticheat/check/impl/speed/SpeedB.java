package rip.warzone.anticheat.check.impl.speed;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

import java.util.Collection;
import java.util.UUID;

public class SpeedB extends PositionCheck {

    private static final UUID MOVE_SPEED=UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    private double horizontalSpeed;
    private double blockFriction;
    private double previousHorizontalMove;

    private int blockFrictionX;
    private int blockFrictionY;
    private int blockFrictionZ;

    public SpeedB(PlayerData playerData) {
        super(playerData, "Speed #2");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        this.horizontalSpeed=this.updateMoveSpeed();

        EntityPlayer entityPlayer=((CraftPlayer) player).getHandle();

        Location to=update.getTo();
        Location from=update.getFrom();

        if (!entityPlayer.server.getAllowFlight() && !player.getAllowFlight()) {
            double dx=to.getX() - from.getX();
            double dy=to.getY() - from.getY();
            double dz=to.getZ() - from.getZ();

            double horizontalSpeed=this.horizontalSpeed;
            double blockFriction=this.blockFriction;

            boolean canSprint=true;
            boolean onGround=entityPlayer.onGround;

            if (onGround) {
                if (canSprint) {
                    horizontalSpeed*=1.3;
                }

                blockFriction*=0.91;
                horizontalSpeed*=0.16277136 / (blockFriction * blockFriction * blockFriction);

                if (dy > 0.0001) {
                    if (canSprint) {
                        horizontalSpeed+=0.2;
                    }

                    MobEffect jumpBoost=entityPlayer.getEffect(MobEffectList.JUMP);

                    // Jump boost falses this
                    if (jumpBoost == null && !entityPlayer.world.c(entityPlayer.boundingBox
                            .grow(0.5, 0.29, 0.5)
                            .d(0.0, 0.3, 0.0)) && dy < 0.3) {
                        horizontalSpeed=0.01;
                    }
                } else if (dy == 0.0) {
                    // Under block
                    if (entityPlayer.world.c(entityPlayer.boundingBox
                            .grow(0.5, 0.0, 0.5)
                            .d(0.0, 0.5, 0.0))) {
//						horizontalSpeed += 0.2;
                    }
                }
            } else {
                horizontalSpeed=canSprint ? 0.026 : 0.02;
                blockFriction=0.91;
            }

            double offsetH=Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dz, 2.0));

            double speedup=(offsetH - this.previousHorizontalMove) / horizontalSpeed;

            double verbose=this.getVl();
            if (player.hasMetadata("modmode")) return;
            if (player.hasMetadata("noflag")) return;
            if (playerData.getTeleportTicks() > 0) return;
            if (this.playerData.getVelocityH() <= 0 && System.currentTimeMillis() > this.playerData.getLastTeleportTime() + 3500L && speedup > 1.08D) {
                if ((verbose+=speedup) >= 30.0D) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("S",speedup),
                            new AlertData("HZ",horizontalSpeed),
                            new AlertData("BF",blockFriction),
                    };
                    this.alert(player, AlertType.RELEASE, alertData, true);
                }
            } else {
                verbose-=0.25D;
            }
            this.setVl(verbose);

            this.previousHorizontalMove=offsetH * blockFriction;

            int blockX=NumberConversions.floor(to.getX());
            int blockY=NumberConversions.floor(to.getY());
            int blockZ=NumberConversions.floor(to.getZ());

            if (blockX != this.blockFrictionX ||
                    blockY != this.blockFrictionY ||
                    blockZ != this.blockFrictionZ) {
                this.blockFriction=entityPlayer.world.getType(blockX, blockY - 1, blockZ).frictionFactor;

                this.blockFrictionX=blockX;
                this.blockFrictionY=blockY;
                this.blockFrictionZ=blockZ;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public double updateMoveSpeed() {
        AttributeModifiable attribute=(AttributeModifiable) (((CraftPlayer) this.getPlayer()).getHandle()).getAttributeInstance(GenericAttributes.d);

        double base=attribute.b();
        double value=base;

        for ( AttributeModifier modifier : (Collection<AttributeModifier>) attribute.a(0) ) {
            value+=modifier.d();
        }

        for ( AttributeModifier modifier : (Collection<AttributeModifier>) attribute.a(1) ) {
            value+=modifier.d() * base;
        }

        for ( AttributeModifier modifier : (Collection<AttributeModifier>) attribute.a(2) ) {
            if (!modifier.a().equals(MOVE_SPEED)) {
                value*=1.0 + modifier.d();
            }
        }

        return value;
    }
}
