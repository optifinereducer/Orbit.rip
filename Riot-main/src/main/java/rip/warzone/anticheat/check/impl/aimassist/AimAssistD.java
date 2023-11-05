package rip.warzone.anticheat.check.impl.aimassist;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.MathUtil;
import rip.warzone.anticheat.util.update.RotationUpdate;

public class AimAssistD extends RotationCheck {
    private float lastYawRate;
    private float lastPitchRate;

    public AimAssistD(PlayerData playerData) {
        super(playerData, "Aim #4");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if (System.currentTimeMillis() - this.playerData.getLastAttackPacket() > 10000L) {
            return;
        }

        float diffPitch=MathUtil.getDistanceBetweenAngles(update.getTo().getPitch(), update.getFrom().getPitch());
        float diffYaw=MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

        float diffPitchRate=Math.abs(this.lastPitchRate - diffPitch);
        float diffYawRate=Math.abs(this.lastYawRate - diffYaw);

        float diffPitchRatePitch=Math.abs(diffPitchRate - diffPitch);
        float diffYawRateYaw=Math.abs(diffYawRate - diffYaw);

        if (diffPitch < 0.009 && diffPitch > 0.001 && diffPitchRate > 1.0 && diffYawRate > 1.0 && diffYaw > 3.0 &&
                this.lastYawRate > 1.5 && (diffPitchRatePitch > 1.0f || diffYawRateYaw > 1.0f)) {

            AlertData[] alertData=new AlertData[]{
                    new AlertData("DPR", diffPitchRate),
                    new AlertData("DYR", diffYawRate),
                    new AlertData("LPR", this.lastPitchRate),
                    new AlertData("LYR", this.lastYawRate),
                    new AlertData("DP", diffPitch),
                    new AlertData("DY", diffYaw),
            };

            this.alert(player, AlertType.EXPERIMENTAL, alertData, true);

            if (!this.playerData.isBanning() && this.playerData.getViolations(this, 1000L * 60 * 10) > 5) {
                this.ban(player);
            }
        }

        this.lastPitchRate=diffPitch;
        this.lastYawRate=diffYaw;
    }

}
