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

public class SpeedA extends PositionCheck {

    private int verbose;
    private double average;

    public SpeedA(PlayerData playerData) {
        super(playerData, "Speed #1");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        double motionX=Math.abs(update.getTo().getX() - update.getFrom().getX());
        double motionZ=Math.abs(update.getTo().getZ() - update.getFrom().getZ());

        double speed=Math.sqrt(Math.pow(motionX, 2) + Math.pow(motionZ, 2));

        if (player.getAllowFlight()
                || playerData.getDeathTicks() > 0
                || System.currentTimeMillis() - playerData.getLastVelocity() < 650) {
            verbose=0;
            return;
        }

        if (player.getVehicle() != null
                || (player.getMaximumNoDamageTicks() < 20
                && player.getNoDamageTicks() >= 1)
                || this.playerData.getVelocityH() <= 0 || player.isFlying()
                || player.getGameMode() != GameMode.SURVIVAL
                || player.getNoDamageTicks() >= 1
                || player.hasMetadata("modmode")
                || player.hasMetadata("noflag")) {
            return;
        }

        double max=0.345;
        max+=0.4 * getPotionEffectLevel(player, PotionEffectType.SPEED);

        average=((average + 14) * speed) / 15;

        if (average > max) {
            if (verbose++ > 4) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("S",speed),
                        new AlertData("MX", motionX),
                        new AlertData("MZ", motionZ),
                };
                this.alert(player, AlertType.RELEASE, alertData, true);
            }
        } else verbose-=verbose > 0 ? 1 : 0;

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
