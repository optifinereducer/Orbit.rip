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

import java.util.Deque;

public class AutoClickerC extends PacketCheck {

    private final Deque<Integer> recentData=new LinkedList<>();

    private int movements;

    public AutoClickerC(PlayerData playerData) {
        super(playerData, "AutoClicker #3");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            if (this.movements < 10 && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
                this.recentData.add(this.movements);
                if (this.recentData.size() == 500) {
                    int outliers=Math.toIntExact(recentData.stream()
                            .mapToInt(i -> i)
                            .filter(i -> i > 3)
                            .count());

                    double vl=this.getVl();
                    if (outliers < 5) {
                        if ((vl+=1.4) >= 4D) {
                            AlertData[] alertData=new AlertData[]
                                    {new AlertData("O", outliers),
                                            new AlertData("VL", vl),};

                            this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
                        }
                    } else {
                        vl-=1.5;
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
