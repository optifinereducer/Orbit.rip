package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInHeldItemSlot;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class BadPacketsF extends PacketCheck {

    private boolean sent;

    public BadPacketsF(PlayerData playerData) {
        super(playerData, "BadPackets #6");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInHeldItemSlot) {
            if (this.sent && this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
                int violations=this.playerData.getViolations(this, 60000L);
                if (!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if (packet instanceof PacketPlayInBlockPlace) {
            this.sent=true;
        } else if (packet instanceof PacketPlayInFlying) {
            this.sent=false;
        }
    }

}
