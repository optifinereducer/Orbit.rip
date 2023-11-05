package rip.warzone.anticheat.check.impl.fly;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PositionCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public class FlyC extends PositionCheck {

    public FlyC(PlayerData playerData) {
        super(playerData, "Flight #3");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate type) {
        double motionY=Math.abs(type.getTo().getY() - type.getFrom().getY());
        int verbose=(int) this.getVl();
        if (motionY < 0.1 && !this.playerData.isInLiquid()
                && !player.getAllowFlight()
                && !this.playerData.isOnGround()
                && this.playerData.getVelocityV() == 0) {
            if (verbose++ > 5) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("MY", motionY)
                };
                alert(player, AlertType.RELEASE, alertData, true);
            }
        } else verbose=Math.max(0, verbose - 1);

        setVl(verbose);
    }
}
