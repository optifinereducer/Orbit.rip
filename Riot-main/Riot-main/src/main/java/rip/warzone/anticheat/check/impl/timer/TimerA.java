package rip.warzone.anticheat.check.impl.timer;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

import java.util.Deque;
import java.util.LinkedList;

public class TimerA extends PacketCheck {

    private final Deque<Long> delays;
    private long lastPacketTime;

    public TimerA(PlayerData playerData) {
        super(playerData, "Timer #1");
        this.delays=new LinkedList<>();
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInFlying
                && !this.playerData.isAllowTeleport()
                && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L) {
            this.delays.add(System.currentTimeMillis() - this.lastPacketTime);
            if (this.delays.size() == 40) {
                double average=0.0;
                for ( long l : this.delays ) {
                    average+=l;
                }
                average/=this.delays.size();
                double vl=this.getVl();
                if (average <= 49.0) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("AVG", average),
                            new AlertData("R", 50.0 / average),
                            new AlertData("VL", vl)
                    };

                    if ((vl+=1.25) >= 4.0 && this.alert(player, AlertType.RELEASE, alertData, false)) {
                    }
                } else {
                    vl-=0.5;
                }
                this.setVl(vl);
                this.delays.clear();
            }
            this.lastPacketTime=System.currentTimeMillis();
        }
    }

}

