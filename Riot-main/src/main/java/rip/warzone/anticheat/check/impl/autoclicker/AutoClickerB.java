package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.MathUtil;

import java.util.Deque;
import java.util.LinkedList;

public class AutoClickerB extends PacketCheck {

    private final Deque<Integer> recentData=new LinkedList<>();

    private int movements;

    public AutoClickerB(PlayerData playerData) {
        super(playerData, "AutoClicker #2");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            if (this.movements < 10 && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
                this.recentData.add(this.movements);
                if (this.recentData.size() == 50) {
                    double stdDev= MathUtil.stDeviation(this.recentData);

                    int vl=(int) this.getVl();
                    if (stdDev < 0.48) {
                        if (++vl >= 5) {
                            AlertData[] alertData=new AlertData[]
                                    {new AlertData("STDEV", stdDev),
                                            new AlertData("CPS", 1000.0D / (MathUtil.average(this.recentData) * 50.0D)),
                                    };
                            this.alert(player, AlertType.RELEASE, alertData, true);
                            int violations=this.playerData.getViolations(this, 60000L);

                            if (!this.playerData.isBanning() && violations > 15) {
                                this.ban(player);
                            }
                        } else {
                            vl--;
                        }

                        this.setVl(vl);

                        this.recentData.clear();
                    }
                }
                this.movements=0;
            } else if (packet instanceof PacketPlayInFlying) {
                this.movements++;
            }
        }
    }
}
