package rip.warzone.anticheat.check.impl.aimassist;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.Verbose;
import rip.warzone.anticheat.util.update.RotationUpdate;

public class AimAssistH extends RotationCheck {

    private final Verbose verbose=new Verbose();

    public AimAssistH(PlayerData playerData) {
        super(playerData, "Aim #8");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate update) {

        double pitch=Math.abs(update.getTo().getPitch() - update.getFrom().getPitch());
        double yaw=Math.abs(update.getTo().getYaw() - update.getFrom().getYaw());

        if (pitch > 0 || yaw > 0) {
            playerData.hasLooked=true;
            double offset=pitch % 1;
            double value=pitch % offset;

            double offset1=yaw % 1;
            double value1=yaw % offset1;

            if (value == 0 && pitch < 0.1 && pitch > 0 && value1 < 0.1 && yaw > 1.4) {
                AlertData[] data=new AlertData[]{
                        new AlertData("P", pitch),
                        new AlertData("Y", yaw),
                        new AlertData("V", value),
                        new AlertData("V2", value1)
                };
                if (verbose.flag(2, 550)) alert(player, AlertType.RELEASE, data, true);
            }
        } else {
            playerData.hasLooked=false;
        }

    }

}