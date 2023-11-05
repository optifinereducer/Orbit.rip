package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.EnumEntityUseAction;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.CustomLocation;

public class KillAuraE extends PacketCheck {

    private long lastAttack;
    private boolean attack;

    public KillAuraE(PlayerData playerData) {
        super(playerData, "KillAura #5");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        double vl=this.getVl();
        if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).c() == EnumEntityUseAction.ATTACK && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L &&
                !this.playerData.isAllowTeleport()) {
            CustomLocation lastMovePacket=this.playerData.getLastMovePacket();
            if (lastMovePacket == null) {
                return;
            }
            long delay=System.currentTimeMillis() - lastMovePacket.getTimestamp();
            if (delay <= 25.0) {
                this.lastAttack=System.currentTimeMillis();
                this.attack=true;
            } else {
                vl-=0.25;
            }
        } else if (packet instanceof PacketPlayInFlying && this.attack) {
            long time=System.currentTimeMillis() - this.lastAttack;
            if (time >= 25L) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("T", time),
                        new AlertData("VL", vl)
                };

                if (++vl >= 10.0 && this.alert(player, AlertType.EXPERIMENTAL, alertData, false)) {
                }
            } else {
                vl-=0.25;
            }
            this.attack=false;
        }
        this.setVl(vl);
    }

}
