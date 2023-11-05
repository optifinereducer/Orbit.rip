package rip.warzone.anticheat.check.impl.badpackets;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class BadPacketsE extends PacketCheck {

    public BadPacketsE(PlayerData playerData) {
        super(playerData, "BadPackets #5");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).g() == BadPacketsE.RELEASE_USE_ITEM && this.playerData.isPlacing() && this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
            int violations=this.playerData.getViolations(this, 60000L);
            if (!this.playerData.isBanning() && violations > 2) {
                this.ban(player);
            }
        }
    }

}
