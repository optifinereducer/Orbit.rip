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

public class AutoClickerE extends PacketCheck {

    private final Deque<Integer> recentData=new LinkedList<>();
    private int movements;

    public AutoClickerE(PlayerData playerData) {
        super(playerData, "AutoClicker #5");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            if (this.movements < 10 && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
                this.recentData.add(this.movements);
                if (this.recentData.size() == 500) {
                    int outliers=Math.toIntExact(recentData.stream()
                            .mapToInt(i -> 1)
                            .filter(i -> i > 3)
                            .count());

                    double vl=this.getVl();
                    if (outliers <= 2) {
                        if ((vl+=1.4) >= 3.2) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("O", outliers),
                                    new AlertData("VL", vl),
                                    new AlertData("CPS", 1000.0D / (MathUtil.average(this.recentData) * 50.0D))
                            };
                            this.alert(player, AlertType.RELEASE, alertData, true);
                        }
                    } else {
                        vl-=0.65;
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

