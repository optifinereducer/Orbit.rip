package rip.warzone.anticheat.check.impl.killaura;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.AbstractCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraN extends AbstractCheck<int[]> {

    private int doubleSwings;
    private int doubleAttacks;
    private int bareSwings;

    public KillAuraN(PlayerData playerData) {
        super(playerData, int[].class, "KillAura #14");
    }

    @Override
    public void handleCheck(Player player, int[] ints) {
        int swings=ints[0];
        int attacks=ints[1];
        if (swings > 1 && attacks == 0) {
            ++this.doubleSwings;
        } else if (swings == 1 && attacks == 0) {
            ++this.bareSwings;
        } else if (attacks > 1) {
            ++this.doubleAttacks;
        }
        if (this.doubleSwings + this.doubleAttacks == 20) {
            double vl=this.getVl();
            if (this.doubleSwings == 0) {
                if (this.bareSwings > 10 && ++vl > 3.0) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("BS", this.bareSwings),
                            new AlertData("VL", vl)
                    };

                    this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
                }
            } else {
                --vl;
            }
            this.setVl(vl);
            this.doubleSwings=0;
            this.doubleAttacks=0;
            this.bareSwings=0;
        }
    }

}
