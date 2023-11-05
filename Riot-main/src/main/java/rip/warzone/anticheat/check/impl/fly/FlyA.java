package rip.warzone.anticheat.check.impl.fly;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class FlyA extends PositionCheck {

    public FlyA(PlayerData playerData) {
        super(playerData, "Flight #1");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        int vl=(int) this.getVl();

        if (!this.playerData.isInLiquid() && !this.playerData.isOnGround() && this.playerData.getVelocityV() == 0) {
            if (update.getFrom().getY() >= update.getTo().getY()) {
                return;
            }

            double distance=update.getTo().getY() - this.playerData.getLastGroundY();
            double limit=2.0;

            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                for ( PotionEffect effect : player.getActivePotionEffects() ) {
                    if (effect.getType().equals(PotionEffectType.JUMP)) {
                        int level=effect.getAmplifier() + 1;
                        limit+=Math.pow(level + 4.2, 2.0) / 16.0;
                        break;
                    }
                }
            }

            if (distance > limit) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("VL", vl)
                };

                if (++vl >= 10 && this.alert(player, AlertType.RELEASE, alertData, true)) {
                    int violations=this.playerData.getViolations(this, 60000L);

                    if (!this.playerData.isBanning() && violations > 8) {
                        this.ban(player);
                    }
                }
            } else {
                vl=0;
            }
        } else {
            vl=0;
        }

        this.setVl(vl);
    }

}
