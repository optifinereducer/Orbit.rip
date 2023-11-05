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

public class SpeedC extends PositionCheck {


    public SpeedC(PlayerData playerData) {
        super(playerData, "Speed #3");
    }

    private boolean lastGround;
    private double speed;

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        if (player.getAllowFlight()
                || !player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        double x=Math.abs(Math.abs(update.getTo().getX()) - Math.abs(update.getFrom().getX()));
        double z=Math.abs(Math.abs(update.getTo().getZ()) - Math.abs(update.getFrom().getZ()));
        speed=Math.sqrt(x * x + z * z);


        double max=0.64f;

        for ( PotionEffect effect : this.getPlayer().getActivePotionEffects() ) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                max+=effect.getAmplifier() + 1;
            }
        }

        boolean ground=playerData.isOnGround();

        if (ground && !lastGround && this.speed > max) {
            if (player.hasMetadata("modmode")) return;
            if (player.hasMetadata("noflag")) return;
            AlertData[] alertData=new AlertData[]{
                    new AlertData("S",speed),
            };
            this.alert(player, AlertType.RELEASE, alertData, false);
        }

        this.lastGround=ground;
    }
}

