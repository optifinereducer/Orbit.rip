package rip.warzone.anticheat.check.impl.aimassist;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.MathUtil;
import rip.warzone.anticheat.util.update.RotationUpdate;

public class AimAssistC extends RotationCheck {

    public AimAssistC(PlayerData playerData) {
        super(playerData, "Aim #3");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if (System.currentTimeMillis() - this.playerData.getLastAttackPacket() > 10000L) {
            return;
        }

        float diffYaw=MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());
        double vl=this.getVl();

        AlertData[] alertData=new AlertData[]{
                new AlertData("Y", diffYaw),
                new AlertData("VL", vl)
        };

        if (update.getFrom().getPitch() == update.getTo().getPitch() && diffYaw >= 3.0f && update.getFrom().getPitch() != 90.0f && update.getTo().getPitch() != 90.0f) {
            if ((vl+=0.9) >= 6.3) {
                this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
            }
        } else {
            vl-=1.6;
        }

        this.setVl(vl);
    }

}
