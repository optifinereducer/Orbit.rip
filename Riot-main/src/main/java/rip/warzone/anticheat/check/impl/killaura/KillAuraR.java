package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraR extends PacketCheck {

    private boolean sentUseEntity;

    public KillAuraR(PlayerData playerData) {
        super(playerData, "KillAura #18");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInBlockPlace) {
            if (((PacketPlayInBlockPlace) packet).getFace() != 255 && this.sentUseEntity && this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
                int violations=this.playerData.getViolations(this, 60000L);
                if (!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            this.sentUseEntity=true;
        } else if (packet instanceof PacketPlayInFlying) {
            this.sentUseEntity=false;
        }
    }

}
