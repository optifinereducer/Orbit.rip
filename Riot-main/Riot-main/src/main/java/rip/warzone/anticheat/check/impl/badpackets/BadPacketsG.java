package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInEntityAction;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class BadPacketsG extends PacketCheck {

    private int lastAction;

    public BadPacketsG(PlayerData playerData) {
        super(playerData, "BadPackets #7");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInEntityAction) {
            int playerAction=((PacketPlayInEntityAction) packet).d();
            if (playerAction == BadPacketsG.START_SPRINTING || playerAction == BadPacketsG.STOP_SPRINTING) {
                if (this.lastAction == playerAction && this.playerData.getLastAttackPacket() + 10000L > System.currentTimeMillis() && this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
                    int violations=this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > 2) {
                        this.ban(player);
                    }
                }
                this.lastAction=playerAction;
            }
        }
    }

}
