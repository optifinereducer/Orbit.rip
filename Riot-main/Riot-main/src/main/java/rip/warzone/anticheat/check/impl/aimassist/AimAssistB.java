package rip.warzone.anticheat.check.impl.aimassist;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.MathUtil;
import rip.warzone.anticheat.util.update.RotationUpdate;

public class AimAssistB extends RotationCheck {

    private float suspiciousYaw;

    public AimAssistB(PlayerData playerData) {
        super(playerData, "Aim #2");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if (System.currentTimeMillis() - this.playerData.getLastAttackPacket() > 10000L) {
            return;
        }

        float diffYaw=MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

        if (diffYaw > 1.0f && Math.round(diffYaw * 10.0f) * 0.1f == diffYaw && Math.round(diffYaw) != diffYaw && diffYaw % 1.5f != 0.0f) {
            AlertData[] alertData=new AlertData[]{
                    new AlertData("Y", diffYaw)
            };

            if (diffYaw == this.suspiciousYaw && this.alert(player, AlertType.RELEASE, alertData, true)) {
                int violations=this.playerData.getViolations(this, 60000L);

                if (!this.playerData.isBanning() && violations > 20) {
                    this.ban(player);
                }
            }

            this.suspiciousYaw=Math.round(diffYaw * 10.0f) * 0.1f;
        } else {
            this.suspiciousYaw=0.0f;
        }
    }

}
