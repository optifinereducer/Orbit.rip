package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraO extends PacketCheck {

    private boolean sent;

    public KillAuraO(PlayerData playerData) {
        super(playerData, "KillAura #15");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).c() == EnumEntityUseAction.ATTACK) {
            if (this.sent && this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
                int violations=this.playerData.getViolations(this, 60000L);
                if (!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if (packet instanceof PacketPlayInCloseWindow) {
            this.sent=true;
        } else if (packet instanceof PacketPlayInFlying) {
            this.sent=false;
        }
    }

}
