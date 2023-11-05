package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInSteerVehicle;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class BadPacketsI extends PacketCheck {

    private float lastYaw;
    private float lastPitch;
    private boolean ignore;

    public BadPacketsI(PlayerData playerData) {
        super(playerData, "BadPackets #9");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying flying=(PacketPlayInFlying) packet;
            if (!flying.j() && flying.k()) {
                if (this.lastYaw == flying.g() && this.lastPitch == flying.h()) {
                    if (!this.ignore) {
                        this.alert(player, AlertType.EXPERIMENTAL, new AlertData[0], false);
                    }

                    this.ignore=false;
                }

                this.lastYaw=flying.g();
                this.lastPitch=flying.h();
            } else {
                this.ignore=true;
            }
        } else if (packet instanceof PacketPlayInSteerVehicle) {
            this.ignore=true;
        }
    }

}
