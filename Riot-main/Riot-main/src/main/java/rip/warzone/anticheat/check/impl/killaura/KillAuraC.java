package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.CustomLocation;
import rip.warzone.anticheat.util.MathUtil;

public class KillAuraC extends PacketCheck {

    private float lastYaw;

    public KillAuraC(PlayerData playerData) {
        super(playerData, "KillAura #3");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (this.playerData.getLastTarget() == null) {
            return;
        }
        if (packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying flying=(PacketPlayInFlying) packet;
            if (flying.k() && !this.playerData.isAllowTeleport()) {
                CustomLocation targetLocation=this.playerData.getLastPlayerPacket(this.playerData.getLastTarget(), MathUtil.pingFormula(this.playerData.getPing()));
                if (targetLocation == null) {
                    return;
                }
                CustomLocation playerLocation=this.playerData.getLastMovePacket();
                if (playerLocation.getX() == targetLocation.getX()) {
                    return;
                }
                if (targetLocation.getZ() == playerLocation.getZ()) {
                    return;
                }
                float yaw=flying.g();
                if (yaw != this.lastYaw) {
                    float bodyYaw=MathUtil.getDistanceBetweenAngles(yaw, MathUtil.getRotationFromPosition(playerLocation, targetLocation)[0]);
                    if (bodyYaw == 0.0f && this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
                        int violations=this.playerData.getViolations(this, 60000L);
                        if (!this.playerData.isBanning() && violations > 5) {
                            this.ban(player);
                        }
                    }
                }
                this.lastYaw=yaw;
            }
        }
    }

}
