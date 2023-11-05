package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.LinkedList;
import rip.warzone.anticheat.util.MathUtil;

import java.util.Deque;

public class AutoClickerD extends PacketCheck {
    private final Deque<Integer> recentData=new LinkedList<>();

    private int movements;

    public AutoClickerD(PlayerData playerData) {
        super(playerData, "AutoClicker #4");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            if (this.movements < 10 && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
                this.recentData.add(this.movements);
                if (this.recentData.size() == 250) {
                    double stdDev=MathUtil.stDeviation(this.recentData);

                    double vl=this.getVl();
                    if (stdDev < 0.55) {
                        if (++vl > 4) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("STD", stdDev),
                                    new AlertData("CPS", 1000.0D / ((MathUtil.average(this.recentData)) * 50.0D)),
                                    new AlertData("VL", vl),
                            };
                            this.alert(player, AlertType.RELEASE, alertData, false);
                        }
                    } else {
                        vl-=2.4;
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
