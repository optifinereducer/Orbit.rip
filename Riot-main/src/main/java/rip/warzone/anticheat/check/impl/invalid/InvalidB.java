package rip.warzone.anticheat.check.impl.invalid;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class InvalidB extends PositionCheck {

    private double verbose;
    private boolean fallen;

    public InvalidB(PlayerData playerData) {
        super(playerData, "Invalid #2");
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
                || player.getAllowFlight() || this.playerData.isInLiquid()
                || !this.playerData.isWasUnderBlock()) {
            verbose=0;
            fallen=false;
        } else {
            String tags="";

            if (motionY < -0.05) {
                fallen=true;
            }

            if (motionY >= 0 && playerData.getAirTicks() > 5) {
                tags+="InvalidY ";
                verbose+=0.5f;
            }

            if (fallen && motionY > -0.08 && motionY != 0) {
                tags+="Glide ";
                verbose+=0.25f;
            }

            if (motionY < -0.6 && playerData.getAirTicks() <= 1) {
                tags+="FastFall";
                verbose+=0.5f;
            }

            if (verbose > 1) {
                AlertData[] data=new AlertData[]{
                        new AlertData("MY", motionY),
                        new AlertData("Verbose", verbose),
                        new AlertData("T", tags)
                };
                alert(player, AlertType.EXPERIMENTAL, data, false);
            }

        }


    }
}
