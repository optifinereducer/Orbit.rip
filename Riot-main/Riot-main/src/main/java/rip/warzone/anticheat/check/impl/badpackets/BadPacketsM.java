package rip.warzone.anticheat.check.impl.badpackets;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class BadPacketsM extends PositionCheck {

    public BadPacketsM(PlayerData playerData) {
        super(playerData, "BadPackets #12");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        double height=0.9;
        double difference=update.getTo().getY() - update.getFrom().getY();
        double vl=this.getVl();

        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            for ( PotionEffect effect : player.getActivePotionEffects() ) {
                if (effect.getType().equals(PotionEffectType.JUMP)) {
                    int level=effect.getAmplifier() + 1;
                    height+=Math.pow(level + 4.2, 2.0) / 16.0;
                    break;
                }
            }
        }

        if (difference > height) {
            if (player.hasMetadata("noflag")) return;
            if (player.hasMetadata("modmode")) return;
            AlertData[] alertData=new AlertData[]{
                    new AlertData("height", height),
                    new AlertData("diff", difference)
            };
            if (++vl >= 15 && this.alert(player, AlertType.EXPERIMENTAL, alertData, true)) {
            }
            this.playerData.setCheckVl(vl, this);
        }
    }
}
