package rip.warzone.anticheat.check.impl.speed;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

import java.text.DecimalFormat;

public class SpeedG extends PositionCheck {

    public SpeedG(PlayerData playerData) {
        super(playerData, "Speed #7");
    }

    private int verbose;

    @Override
    public void handleCheck(Player player, PositionUpdate update) {

        double motionX=Math.abs(update.getTo().getX() - update.getFrom().getX());
        double motionZ=Math.abs(update.getTo().getZ() - update.getFrom().getZ());

        double speed=Math.sqrt(Math.pow(motionX, 2) + Math.pow(motionZ, 2));

        if (player.getAllowFlight()
                || playerData.getDeathTicks() > 0
                || (System.currentTimeMillis() - this.playerData.getVelocityH() <= 0)
                || playerData.getIceTimer().hasNotPassed(20)
                || playerData.isOnStairs()
                || playerData.getBlockAboveTimer().hasNotPassed(15)) {
            verbose=0;
            return;
        }

        if (player.getVehicle() != null
                || (player.getMaximumNoDamageTicks() < 20
                && player.getNoDamageTicks() >= 1) || player.isFlying()
                || player.getGameMode() != GameMode.SURVIVAL ||
                player.getNoDamageTicks() >= 1
                || player.hasMetadata("modmode")
                || player.hasMetadata("noflag")) {
            return;
        }

        if (speed > getBaseSpeed(player) && playerData.getAirTicks() > 2 && ((speed / getBaseSpeed(player)) - 1) > 0.025) {
            AlertData[] data=new AlertData[]{
                    new AlertData("S", speed),
                    new AlertData("PI", new DecimalFormat("#.##").format(((speed / getBaseSpeed(player)) - 1)) + "%"),
                    new AlertData("AT", playerData.getAirTicks())
            };
            alert(player, AlertType.RELEASE, data, true);

        }


    }

    /**
     * Checked base speed method to the method in ruby. (hopefully helps detect more cheaters)
     */

    private float getBaseSpeed(Player player) {
        float magic=0.351f;
        return magic + (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.068f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    private int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for ( PotionEffect pe : player.getActivePotionEffects() ) {
            if (pe.getType().getName().equals(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }
}
