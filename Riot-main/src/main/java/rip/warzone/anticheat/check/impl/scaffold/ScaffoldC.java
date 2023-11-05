package rip.warzone.anticheat.check.impl.scaffold;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class ScaffoldC extends PacketCheck {

    private int looks;
    private int stage;

    public ScaffoldC(PlayerData playerData) {
        super(playerData, "Placement #2");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        double vl=this.getVl();

        if (packet instanceof PacketPlayInLook) {
            if (this.stage == 0) {
                ++this.stage;
            } else if (this.stage == 4) {
                if ((vl+=1.75) > 3.5) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("VL", vl)
                    };

                    this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
                }

                this.stage=0;
            } else {
                this.looks=0;
                this.stage=0;
                vl-=0.2;
            }
        } else if (packet instanceof PacketPlayInBlockPlace) {
            if (this.stage == 1) {
                ++this.stage;
            } else {
                this.looks=0;
                this.stage=0;
            }
        } else if (packet instanceof PacketPlayInArmAnimation) {
            if (this.stage == 2) {
                ++this.stage;
            } else {
                this.looks=0;
                this.stage=0;
                vl-=0.2;
            }
        } else if (packet instanceof PacketPlayInPositionLook || packet instanceof PacketPlayInPosition) {
            if (this.stage == 3) {
                if (++this.looks == 3) {
                    this.stage=4;
                    this.looks=0;
                }
            } else {
                this.looks=0;
                this.stage=0;
            }
        }

        this.setVl(vl);
    }

}
