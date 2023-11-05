package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraQ extends PacketCheck {

    private boolean sentAttack;
    private boolean sentInteract;

    public KillAuraQ(PlayerData playerData) {
        super(playerData, "KillAura #17");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInBlockPlace) {
            if (this.sentAttack && !this.sentInteract && this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
                int violations=this.playerData.getViolations(this, 60000L);
                if (!this.playerData.isBanning() && violations > 2) {
                    this.ban(player);
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            EnumEntityUseAction action=((PacketPlayInUseEntity) packet).c();
            if (action == EnumEntityUseAction.ATTACK) {
                this.sentAttack=true;
            } else if (action == EnumEntityUseAction.INTERACT) {
                this.sentInteract=true;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            boolean b=false;
            this.sentInteract=false;
            this.sentAttack=false;
        }
    }

}
