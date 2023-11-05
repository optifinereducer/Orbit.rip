package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class BadPacketsB extends PacketCheck {

    public BadPacketsB(PlayerData playerData) {
        super(playerData, "BadPackets #2");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInFlying && Math.abs(((PacketPlayInFlying) packet).h()) > 90.0f && this.alert(player, AlertType.RELEASE, new AlertData[0], false) && !this.playerData.isBanning() && !this.playerData.isRandomBan()) {
            this.randomBan(player, 200.0);
        }
    }

}
