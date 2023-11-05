package rip.warzone.anticheat.banwave.checking;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.check.AbstractCheck;
import rip.warzone.anticheat.player.PlayerData;

import java.util.ArrayList;

public class PlayerChecker {

    public static ResultTypes checkPlayer(Player player) {

        PlayerData data= AntiCheat.instance.getPlayerDataManager().getPlayerData(player);
        ArrayList<AbstractCheck> flagged=data.flaggedChecks;

        for ( AbstractCheck check : flagged ) {
            String name=check.getName().toLowerCase();

            double percentage=(data.getViolations(check) / 20) * 100;

            //Checking if they have a high percentage of a check and if they do they will fail.

            if (percentage > 50 && data.getViolations(check) > 2
                    && !name.contains("timer")) {
                return ResultTypes.FAILED;
            }
            if (data.getViolations(check) > 8) {
                return ResultTypes.UNSURE;
            }

            //TODO make a way to automate checking logs of a player to see if they are cheating - might need machinelearning not sure.

        }

        return ResultTypes.PASS;
    }

}
