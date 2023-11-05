package rip.warzone.anticheat.check.impl.velocity;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class VelocityB extends PositionCheck {

    public VelocityB(PlayerData playerData) {
        super(playerData, "Velocity #2");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        double offsetY=update.getTo().getY() - update.getFrom().getY();

        if (this.playerData.getVelocityY() > 0.0
                && this.playerData.isWasOnGround()
                && !this.playerData.isUnderBlock()
                && !this.playerData.isWasUnderBlock()
                && !this.playerData.isInLiquid()
                && !this.playerData.isWasInLiquid()
                && !this.playerData.isInWeb()
                && !this.playerData.isWasInWeb()
                && !this.playerData.isOnStairs()
                && offsetY > 0.0 && offsetY < 0.41999998688697815 && update.getFrom().getY() % 1.0 == 0.0) {

            double ratioY=offsetY / this.playerData.getVelocityY();
            int vl=(int) this.getVl();
            if (ratioY < 0.99) {
                int percent=(int) Math.round(ratioY * 100.0);
                AlertData[] alertData=new AlertData[]{
                        new AlertData("P", percent + "%"),
                        new AlertData("VL", vl)
                };

                if (++vl >= 5 && this.alert(player, AlertType.RELEASE, alertData, false) && !this.playerData.isBanning() && vl >= 15) {
                    this.ban(player);
                }
            } else {
                --vl;
            }
            this.setVl(vl);
        }
    }

}
