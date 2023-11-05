package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.EnumEntityUseAction;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerG extends PacketCheck {

    private static final int HIGH_CLICK_THRESHOLD=15;
    private static final int LOW_CLICK_THRESHHOLD=10;

    private final List<Long> clickData=new ArrayList<>();
    private int lastCheck, lastVlDecrement;

    public AutoClickerG(PlayerData playerData) {
        super(playerData, "AutoClicker #7");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        long now=System.currentTimeMillis();

        if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).c() == EnumEntityUseAction.ATTACK) {
            this.clickData.add(now);
        } else if (packet instanceof PacketPlayInFlying) {
            if (++this.lastVlDecrement == 100) {
                this.setVl(this.getVl() - 1);

                this.lastVlDecrement=0;
            }

            if (++this.lastCheck == 20) {
                this.clickData.removeIf(time -> time == null || now - time > 1000L);

                double vl=this.getVl();

                int size=this.clickData.size();
                if (size >= 100) {
                    vl+=10;

                    AlertData[] alertData=new AlertData[]{
                    };

                    this.alert(player, AlertType.EXPERIMENTAL, alertData, true);
                    this.clickData.clear();

                    this.setVl(vl);
                    return;
                }

                if (size >= HIGH_CLICK_THRESHOLD) {
                    ++vl;
                } else if (size >= LOW_CLICK_THRESHHOLD) {
                    vl+=0.25;
                }

                if (vl > 15) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("S", size),
                            new AlertData("VL", vl)
                    };

                    this.alert(player, AlertType.EXPERIMENTAL, alertData, true);
                }

                this.setVl(vl);

                this.lastCheck=0;
            }
        }
    }
}

