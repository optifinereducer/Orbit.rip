package rip.warzone.anticheat.check.impl.aimassist;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.RotationUpdate;

public class AimAssistE extends RotationCheck {

    private float lastYawRate;
    private float lastPitchRate;

    public AimAssistE(PlayerData playerData) {
        super(playerData, "Aim #5");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if (System.currentTimeMillis() > this.playerData.getLastAttackTime() + 10000L) {
            return;
        }

        float diffYaw=Math.abs(update.getFrom().getYaw() - update.getTo().getYaw());
        float diffPitch=Math.abs(update.getFrom().getPitch() - update.getTo().getPitch());

        float diffPitchRate=Math.abs(this.lastPitchRate - diffPitch);
        float diffYawRate=Math.abs(this.lastYawRate - diffYaw);

        float diffPitchRatePitch=Math.abs(diffPitchRate - diffPitch);
        float diffYawRateYaw=Math.abs(diffYawRate - diffYaw);

        if (diffPitch > 0.001 && diffPitch < 0.0094 && diffPitchRate > 1F && diffYawRate > 1F && diffYaw > 3F &&
                this.lastYawRate > 1.5 && (diffPitchRatePitch > 1F || diffYawRateYaw > 1F)) {

            AlertData[] alertData=new AlertData[]{
                    new AlertData("DPR", diffPitchRate),
                    new AlertData("DYR", diffYawRate),
                    new AlertData("LPR", lastPitchRate),
                    new AlertData("LYR", lastYawRate),
                    new AlertData("DP", diffPitch),
                    new AlertData("DY", diffYaw),
                    new AlertData("DPRP", diffPitchRatePitch),
                    new AlertData("DYRY", diffYawRateYaw)
            };

            this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
        }

        this.lastPitchRate=diffPitch;
        this.lastYawRate=diffYaw;
    }

}
