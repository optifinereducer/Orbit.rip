package rip.warzone.anticheat.check.impl.fly;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class FlyB extends PositionCheck {

    public FlyB(PlayerData playerData) {
        super(playerData, "Flight #2");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        int vl=(int) this.getVl();

        if (!this.playerData.isInLiquid() && !this.playerData.isOnGround()) {
            double offsetH=Math.hypot(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ());
            double offsetY=update.getTo().getY() - update.getFrom().getY();

            if (offsetH > 0.0 && offsetY == 0.0) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("H", offsetH),
                        new AlertData("VL", vl)
                };

                if (++vl >= 10 && this.alert(player, AlertType.RELEASE, alertData, true)) {
                    int violations=this.playerData.getViolations(this, 60000L);

                    if (!this.playerData.isBanning() && violations > 15) {
                        this.ban(player);
                    }
                }
            } else {
                vl=0;
            }
        } else {
            vl=0;
        }

        this.setVl(vl);
    }

}
