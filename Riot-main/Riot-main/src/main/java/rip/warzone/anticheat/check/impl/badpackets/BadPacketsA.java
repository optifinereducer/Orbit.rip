package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_7_R4.PacketPlayOutAttachEntity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class BadPacketsA extends PacketCheck {

    private int streak;

    public BadPacketsA(PlayerData playerData) {
        super(playerData, "BadPackets #1");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInFlying) {
            if (((PacketPlayInFlying) packet).j()) {
                this.streak=0;
            } else if (++this.streak > 20 && this.alert(player, AlertType.RELEASE, new AlertData[0], false) && !this.playerData.isBanning()) {
                this.ban(player);

            }
        } else if (packet instanceof PacketPlayInSteerVehicle) {
            this.streak=0;
        } else if (packet instanceof PacketPlayOutAttachEntity)
            this.streak=0;
    }

}
