package rip.warzone.anticheat.check.impl.velocity;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.MathUtil;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class VelocityA extends PositionCheck {

    public VelocityA(PlayerData playerData) {
        super(playerData, "Velocity #1");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        int vl=(int) this.getVl();
        if (this.playerData.getVelocityY() > 0.0
                && !this.playerData.isUnderBlock()
                && !this.playerData.isWasUnderBlock()
                && !this.playerData.isInLiquid()
                && !this.playerData.isWasInLiquid()
                && !this.playerData.isInWeb()
                && !this.playerData.isWasInWeb()
                && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L
                && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L) {
            int threshold=10 + MathUtil.pingFormula(this.playerData.getPing()) * 2;
            if (++vl >= threshold) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("VL", vl)
                };

                if (this.alert(player, AlertType.RELEASE, alertData, true)) {
                    int violations=this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > Math.max(this.playerData.getPing() / 10L, 15L)) {
                        this.ban(player);
                    }
                }
                this.playerData.setVelocityY(0.0);
                vl=0;
            }
        } else {
            vl=0;
        }
        this.setVl(vl);
    }

}
