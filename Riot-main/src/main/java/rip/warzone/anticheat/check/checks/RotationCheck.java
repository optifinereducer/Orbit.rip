package rip.warzone.anticheat.check.checks;

import rip.warzone.anticheat.check.AbstractCheck;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.update.RotationUpdate;

public abstract class RotationCheck extends AbstractCheck<RotationUpdate> {

    public RotationCheck(PlayerData playerData, String name) {
        super(playerData, RotationUpdate.class, name);
    }


}
