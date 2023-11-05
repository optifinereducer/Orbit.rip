package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class BadPacketsL extends PacketCheck {

    private boolean sent;
    private boolean vehicle;

    public BadPacketsL(PlayerData playerData) {
        super(playerData, "BadPackets #11");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInFlying) {
            if (this.sent) {
                this.alert(player, AlertType.EXPERIMENTAL, new AlertData[0], false);
            }
            this.vehicle=false;
            this.sent=false;

        } else if (packet instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace blockPlace=(PacketPlayInBlockPlace) packet;
            if (blockPlace.getFace() == 255) {
                ItemStack itemStack=blockPlace.getItemStack();
                if (itemStack != null && itemStack.getName().toLowerCase().contains("sword") && this.playerData.isSprinting() && !this.vehicle) {
                    this.sent=true;
                }
            }
        } else if (packet instanceof PacketPlayInEntityAction && ((PacketPlayInEntityAction) packet).d() == BadPacketsL.STOP_SPRINTING) {
            this.sent=false;
        } else if (packet instanceof PacketPlayInSteerVehicle) {
            this.vehicle=true;
        }
    }
}

