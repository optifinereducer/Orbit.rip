package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraK extends PacketCheck {

    private int ticksSinceStage;
    private int streak;
    private int stage;

    public KillAuraK(PlayerData playerData) {
        super(playerData, "KillAura #11");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation) {
            if (this.stage == 0) {
                this.stage=1;
            } else {
                boolean b=false;
                this.stage=0;
                this.streak=0;
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            if (this.stage == 1) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (packet instanceof PacketPlayInPositionLook) {
            if (this.stage == 2) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (packet instanceof PacketPlayInPosition) {
            if (this.stage == 3) {
                if (++this.streak >= 15) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("STR", this.streak)
                    };

                    this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
                }
                this.ticksSinceStage=0;
            }
            this.stage=0;
        }
        if (packet instanceof PacketPlayInFlying && ++this.ticksSinceStage > 40) {
            this.streak=0;
        }
    }

}
