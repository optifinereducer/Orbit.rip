package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraG extends PacketCheck {

    private int stage;

    public KillAuraG(PlayerData playerData) {
        super(playerData, "KillAura #7");
        this.stage=0;
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        int calculusStage=this.stage % 6;
        if (calculusStage == 0) {
            if (packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (calculusStage == 1) {
            if (packet instanceof PacketPlayInUseEntity) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (calculusStage == 2) {
            if (packet instanceof PacketPlayInEntityAction) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (calculusStage == 3) {
            if (packet instanceof PacketPlayInFlying) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (calculusStage == 4) {
            if (packet instanceof PacketPlayInEntityAction) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (calculusStage == 5) {
            if (packet instanceof PacketPlayInFlying) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("S", this.stage)
                };

                if (++this.stage >= 30 && this.alert(player, AlertType.RELEASE, alertData, true)) {
                    int violations=this.playerData.getViolations(this, 60000L);
                    if (!this.playerData.isBanning() && violations > 5) {
                        this.ban(player);
                    }
                }
            } else {
                this.stage=0;
            }
        }
    }
}
