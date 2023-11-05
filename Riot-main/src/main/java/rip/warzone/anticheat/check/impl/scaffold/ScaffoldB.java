package rip.warzone.anticheat.check.impl.scaffold;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.CustomLocation;

public class ScaffoldB extends PacketCheck {

    private long lastPlace;
    private boolean place;

    public ScaffoldB(PlayerData playerData) {
        super(playerData, "Placement #1");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        double vl=this.getVl();
        if (packet instanceof PacketPlayInBlockPlace && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && !this.playerData.isAllowTeleport()) {
            CustomLocation lastMovePacket=this.playerData.getLastMovePacket();
            if (lastMovePacket == null) {
                return;
            }
            long delay=System.currentTimeMillis() - lastMovePacket.getTimestamp();
            if (delay <= 25.0) {
                this.lastPlace=System.currentTimeMillis();
                this.place=true;
            } else {
                vl-=0.25;
            }
        } else if (packet instanceof PacketPlayInFlying && this.place) {
            long time=System.currentTimeMillis() - this.lastPlace;
            if (time >= 25L) {
                if (++vl >= 10.0) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("T", time),
                            new AlertData("VL", vl)
                    };

                    this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
                }
            } else {
                vl-=0.25;
            }
            this.place=false;
        }
        this.setVl(vl);
    }

}
