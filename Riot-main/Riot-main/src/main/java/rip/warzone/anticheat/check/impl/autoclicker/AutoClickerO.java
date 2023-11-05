package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

import java.util.Deque;
import java.util.LinkedList;

public class AutoClickerO extends PacketCheck {

    private final Deque<Long> ticks=new LinkedList<>();
    private double lastDeviation;

    private double buffer=0.0;

    public AutoClickerO(PlayerData playerData) {
        super(playerData, "AutoClicker #15");
    }

    @Override
    public void handleCheck(Player player, Packet type) {
        if (type instanceof PacketPlayInArmAnimation) {
            if (!this.playerData.isDigging()) ticks.add((long) (this.playerData.getCurrentTick() * 50.0));
            if (ticks.size() >= 10) {
                double deviation=getStandardDeviation(ticks.stream().mapToDouble(d -> d).toArray());
                double diff=Math.abs(deviation - lastDeviation);

                if (diff < 10) {
                    if (++buffer > 5) {
                        AlertData[] alertData=new AlertData[]{
                                new AlertData("DD",diff)
                        };
                        this.alert(player, AlertType.RELEASE, alertData, true);
                    }
                } else buffer*=0.75;

                lastDeviation=deviation;
                ticks.clear();
            }
        }
    }

    public static double getStandardDeviation(double[] numberArray) {
        double sum=0.0, deviation=0.0;
        int length=numberArray.length;
        for ( double num : numberArray )
            sum+=num;
        double mean=sum / length;
        for ( double num : numberArray )
            deviation+=Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

}
