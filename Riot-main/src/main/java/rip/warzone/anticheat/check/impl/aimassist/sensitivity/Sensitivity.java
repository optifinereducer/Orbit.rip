package rip.warzone.anticheat.check.impl.aimassist.sensitivity;

import org.bukkit.entity.Player;
import rip.warzone.anticheat.check.checks.RotationCheck;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.EvictingList;
import rip.warzone.anticheat.util.MathUtil;
import rip.warzone.anticheat.util.update.RotationUpdate;

import java.util.HashMap;
import java.util.Map;

public class Sensitivity extends RotationCheck {

    public Sensitivity(PlayerData playerData) {
        super(playerData, "Sensitivity");
    }

    private static final Map<Double, Double> sensitivityMap=new HashMap<>();

    static {
        for ( double d=50.9; d < 204.8; d+=0.7725 ) {
            sensitivityMap.put(d, sensitivityMap.size() * 0.005);
        }
    }

    private final EvictingList<Double> samples=new EvictingList<>(50);
    private double lastDeltaPitch, lastMode, lastConstant, recordedConstant;

    @Override
    public void handleCheck(Player player, RotationUpdate rotationUpdate) {
        final double deltaPitch=Math.abs(rotationUpdate.getTo().getPitch() - rotationUpdate.getFrom().getPitch());

        final long expandedPitch=(long) Math.abs(deltaPitch * MathUtil.EXPANDER);
        final long lastExpandedPitch=(long) Math.abs(lastDeltaPitch * MathUtil.EXPANDER);

        final double pitchGcd=MathUtil.getGcd(expandedPitch, lastExpandedPitch) / MathUtil.EXPANDER;

        samples.add(pitchGcd);
        if (samples.size() == 50) {
            final double mode=MathUtil.getMode(samples);

            final long expandedMode=(long) (mode * MathUtil.EXPANDER);
            final long lastExpandedMode=(long) (lastMode * MathUtil.EXPANDER);

            final double modeGcd=MathUtil.getGcd(expandedMode, lastExpandedMode);
            final double constant=Math.round((Math.cbrt(modeGcd / .15 / 8) - .2 / .6) * 1000.0) / 1000.0;


            if (Math.abs(constant - lastConstant) < 0.01) {
                playerData.setVerifyingSensitivity(false);
                recordedConstant=constant;
            } else {
                playerData.setVerifyingSensitivity(true);
            }

            final double sensitivity=getSensitivity(recordedConstant);
            if (sensitivity > -1) {
                playerData.setSensitivity(sensitivity);
            }

            /*if(!playerData.isVerifyingSensitivity()) {
                Bukkit.broadcastMessage(ChatColor.RED + "" + player.getName() + "'s Sensitivity = " + ChatColor.GREEN + Math.round(sensitivity * 200.0) + "%");
            }*/

            lastConstant=constant;
            lastMode=mode;
            samples.clear();
        }

        lastDeltaPitch=deltaPitch;
    }

    public double getSensitivity(double constant) {
        for ( double val : sensitivityMap.keySet() ) {
            if (Math.abs(val - constant) <= 0.4) {
                return sensitivityMap.get(val);
            }
        }
        return -1;
    }
}
