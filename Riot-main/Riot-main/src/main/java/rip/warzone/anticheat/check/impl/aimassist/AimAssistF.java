package rip.warzone.anticheat.check.impl.aimassist;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.RotationUpdate;

public class AimAssistF extends RotationCheck {


    public AimAssistF(PlayerData playerData) {
        super(playerData, "Aim #6");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        float fromYaw=(update.getFrom().getYaw() - 90) % 360F;
        float toYaw=(update.getTo().getYaw() - 90) % 360F;


        if (fromYaw < 0F)
            fromYaw+=360F;

        if (toYaw < 0F)
            toYaw+=360F;

        double diffYaw=Math.abs(toYaw - fromYaw);

        int vl=(int) this.getVl();

        if (diffYaw > 0D) {
            if (diffYaw % 1 == 0D) {
                if ((vl+=12) > 45) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("VL", vl),
                    };
                    this.alert(player, AlertType.RELEASE, alertData, true);
                }
            } else {
                vl-=8;
            }
        } else {
            vl-=8;
        }
        this.setVl(vl);
    }

}
