package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.BlockPos;

public class AutoClickerJ extends PacketCheck {

    private int stage;

    public AutoClickerJ(PlayerData playerData) {
        super(playerData, "AutoClicker #10");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (this.stage == 0) {
            if (packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            }
        } else if (packet instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig blockDig=(PacketPlayInBlockDig) packet;
            BlockPos blockPos=new BlockPos(blockDig.c(), blockDig.d(), blockDig.e());
            if (this.playerData.getFakeBlocks().contains(blockPos)) {
                this.stage=0;
                return;
            }
            double vl=this.getVl();
            int digType=((PacketPlayInBlockDig) packet).g();
            if (digType == AutoClickerJ.ABORT_DESTROY_BLOCK) {
                if (this.stage == 1) {
                    ++this.stage;
                } else {
                    this.stage=0;
                }
            } else if (digType == AutoClickerJ.START_DESTROY_BLOCK) {
                if (this.stage == 2) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("VL", vl)
                    };

                    if ((vl+=1.4) >= 15.0 && this.alert(player, AlertType.RELEASE, alertData, true)) ;
                } else {
                    this.stage=0;
                    vl-=0.25;
                }
            } else {
                this.stage=0;
            }
            this.setVl(vl);
        } else {
            this.stage=0;
        }
    }
}
