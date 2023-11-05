package rip.warzone.anticheat.check.impl.invalid;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class InvalidA extends PositionCheck {

    private int verbose;

    public InvalidA(PlayerData playerData) {
        super(playerData, "Invalid #1");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate type) {
        double Y=Math.abs(type.getTo().getY() - type.getFrom().getY());

        if (Y > 0.39 && playerData.getAirTicks() == 0 && playerData.getGroundTicks() > 5
                && !playerData.isOnStairs()) {

            if (verbose++ > 3) {
                AlertData[] data=new AlertData[]{
                        new AlertData("Y", Y)
                };
                alert(player, AlertType.RELEASE, data, true);
            }
        } else verbose=Math.max(0, verbose - 1);
    }
}
