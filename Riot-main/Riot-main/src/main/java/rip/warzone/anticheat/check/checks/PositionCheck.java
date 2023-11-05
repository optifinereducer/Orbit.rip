package rip.warzone.anticheat.check.checks;

import rip.warzone.anticheat.check.AbstractCheck;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.PositionUpdate;

public abstract class PositionCheck extends AbstractCheck<PositionUpdate> {

    public PositionCheck(PlayerData playerData, String name) {
        super(playerData, PositionUpdate.class, name);
    }

}
