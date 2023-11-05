package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraH extends PacketCheck {

    private boolean sent;

    public KillAuraH(PlayerData playerData) {
        super(playerData, "KillAura #8");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInBlockDig) {
            int digType=((PacketPlayInBlockDig) packet).g();
            if ((digType == KillAuraH.START_DESTROY_BLOCK || digType == KillAuraH.RELEASE_USE_ITEM) && this.sent && this.alert(player, AlertType.RELEASE, new AlertData[0], true) && !this.playerData.isFakeDigging() &&
                    !this.playerData.isInstantBreakDigging()) {
                int violations=this.playerData.getViolations(this, 60000L);
                if (!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            this.sent=true;
        } else if (packet instanceof PacketPlayInFlying) {
            this.sent=false;
        }
    }

}
