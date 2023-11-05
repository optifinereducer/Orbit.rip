package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

public class AutoClickerQ extends PacketCheck {

    private final Deque<Double> delays=new LinkedList<>();
    private double lastKurtosis;
    private int ticks;
    private double buffer;

    public AutoClickerQ(PlayerData playerData) {
        super(playerData, "AutoClicker #17");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            if (ticks < 10) {
                delays.add((double) ticks);

                if (delays.size() == 20) {
                    double kurtosis=getKurtosis(delays);

                    if (Double.isNaN(kurtosis) || Math.abs(kurtosis - lastKurtosis) < 0.001) {
                        ++buffer;
                        if (buffer > 2) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("K",kurtosis)
                            };
                            this.alert(player, AlertType.EXPERIMENTAL, alertData, true);
                        }
                    } else buffer=0;

                    lastKurtosis=kurtosis;
                    delays.clear();
                }
            }
        } else if (packet instanceof PacketPlayInFlying) {
            ++ticks;
        }
    }

    public double getKurtosis(Collection<? extends Number> data) {
        double sum=0.0;
        int count=0;

        for ( Number number : data ) {
            sum+=number.doubleValue();
            ++count;
        }

        if (count < 3.0) {
            return 0.0;
        }

        double efficiencyFirst=count * (count + 1.0) / ((count - 1.0) * (count - 2.0) * (count - 3.0));
        double efficiencySecond=3.0 * Math.pow(count - 1.0, 2.0) / ((count - 2.0) * (count - 3.0));
        double average=sum / count;

        double variance=0.0;
        double varianceSquared=0.0;

        for ( Number number : data ) {
            variance+=Math.pow(average - number.doubleValue(), 2.0);
            varianceSquared+=Math.pow(average - number.doubleValue(), 4.0);
        }

        return efficiencyFirst * (varianceSquared / Math.pow(variance / sum, 2.0)) - efficiencySecond;
    }

}