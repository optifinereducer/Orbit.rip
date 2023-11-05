package rip.warzone.anticheat.check.impl.velocity;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class VelocityC extends PositionCheck {

    public VelocityC(PlayerData playerData) {
        super(playerData, "Velocity #3");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        double offsetY=update.getTo().getY() - update.getFrom().getY();
        double offsetH=Math.hypot(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ());
        double velocityH=Math.hypot(this.playerData.getVelocityX(), this.playerData.getVelocityZ());

        EntityPlayer entityPlayer=((CraftPlayer) update.getPlayer()).getHandle();

        if (this.playerData.getVelocityY() > 0.0
                && this.playerData.isWasOnGround()
                && !this.playerData.isUnderBlock()
                && !this.playerData.isWasUnderBlock()
                && !this.playerData.isInLiquid()
                && !this.playerData.isWasInLiquid()
                && !this.playerData.isInWeb()
                && !this.playerData.isWasInWeb()
                && update.getFrom().getY() % 1.0 == 0.0 && offsetY > 0.0 && offsetY < 0.41999998688697815 && velocityH > 0.45
                && !entityPlayer.world.c(entityPlayer.boundingBox.clone().grow(1.0, 0.0, 1.0))) {

            double ratio=offsetH / velocityH;
            double vl=this.getVl();
            if (ratio < 0.62) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("P", Math.round(ratio * 100.0) + "%"),
                        new AlertData("VL", vl)
                };

                if ((vl+=1.1) >= 8.0 && this.alert(player, AlertType.RELEASE, alertData, false) && !this.playerData.isBanning() && vl >= 20.0) {
                    this.ban(player);
                }
            } else {
                vl-=0.4;
            }
            this.setVl(vl);
        }
    }

}
