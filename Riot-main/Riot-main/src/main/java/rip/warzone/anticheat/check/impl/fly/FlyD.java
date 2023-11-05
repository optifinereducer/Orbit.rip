package rip.warzone.anticheat.check.impl.fly;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class FlyD extends PositionCheck {

    private boolean fallen;
    private int verbose;

    public FlyD(PlayerData playerData) {
        super(playerData, "Flight #4");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate type) {
        double motionY=type.getTo().getY() - type.getFrom().getY();

        if ((System.currentTimeMillis() - playerData.getLastTeleportTime() <= 500
                || player.getVehicle() != null
                || (player.getMaximumNoDamageTicks() < 20 && player.getNoDamageTicks() >= 3)
                || player.isFlying()
                || player.getGameMode() != GameMode.SURVIVAL
                || player.getNoDamageTicks() >= 3)) {
            verbose=0;
            return;
        }

        if (playerData.isOnGround()
                || System.currentTimeMillis() - playerData.getLastVelocity() < 2000
                || player.getAllowFlight()
                || this.playerData.isInLiquid()
                || !this.playerData.isWasUnderBlock()) {
            verbose=0;
            fallen=false;
        } else {
            if (motionY < 0) {
                fallen=true;
            }
            if (fallen && motionY > -0.06) {
                if (verbose++ > 6) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("F",fallen),
                            new AlertData("MY",motionY),
                    };
                    this.alert(player, AlertType.RELEASE, alertData, true);
                }
            }
        }
    }
}
