package rip.warzone.anticheat.check.impl.aimassist.cinematic;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.GraphUtil;
import rip.warzone.anticheat.util.update.RotationUpdate;

import java.util.List;

public class Cinematic extends RotationCheck {

    private long lastSmooth=0L, lastHighRate=0L;
    private double lastDeltaYaw=0.0d, lastDeltaPitch=0.0d;

    private final List<Double> yawSamples=Lists.newArrayList();
    private final List<Double> pitchSamples=Lists.newArrayList();

    public Cinematic(PlayerData playerData) {
        super(playerData, "Cinematic");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate rotationUpdate) {
        long now=System.currentTimeMillis();

        double deltaYaw=Math.abs(rotationUpdate.getTo().getYaw() - rotationUpdate.getFrom().getYaw()) % 360F;
        double deltaPitch=Math.abs(rotationUpdate.getTo().getPitch() - rotationUpdate.getFrom().getPitch());

        double differenceYaw=Math.abs(deltaYaw - lastDeltaYaw);
        double differencePitch=Math.abs(deltaPitch - lastDeltaPitch);

        double joltYaw=Math.abs(differenceYaw - deltaYaw);
        double joltPitch=Math.abs(differencePitch - deltaPitch);

        boolean cinematic=(now - lastHighRate > 250L) || now - lastSmooth < 9000L;

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            this.lastHighRate=now;
        }

        if (deltaYaw > 0.0 && deltaPitch > 0.0) {
            yawSamples.add(deltaYaw);
            pitchSamples.add(deltaPitch);
        }

        if (yawSamples.size() == 20 && pitchSamples.size() == 20) {
            GraphUtil.GraphResult resultsYaw=GraphUtil.getGraph(yawSamples);
            GraphUtil.GraphResult resultsPitch=GraphUtil.getGraph(pitchSamples);

            int negativesYaw=resultsYaw.getNegatives();
            int negativesPitch=resultsPitch.getNegatives();

            int positivesYaw=resultsYaw.getPositives();
            int positivesPitch=resultsPitch.getPositives();

            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                this.lastSmooth=now;
            }

            yawSamples.clear();
            pitchSamples.clear();
        }

        playerData.setCinematic(cinematic);

        this.lastDeltaYaw=deltaYaw;
        this.lastDeltaPitch=deltaPitch;
    }

}
