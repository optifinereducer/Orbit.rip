package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class AutoClickerL extends PacketCheck {

    private int movements;
    private int failed;
    private int passed;
    private int stage;

    public AutoClickerL(PlayerData playerData) {
        super(playerData, "AutoClicker #12");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && this.playerData.getLastMovePacket() != null && System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L) {
            if (packet instanceof PacketPlayInArmAnimation) {
                if (this.stage == 0 || this.stage == 1) {
                    ++this.stage;
                } else {
                    this.stage=1;
                }
            } else if (packet instanceof PacketPlayInFlying) {
                if (this.stage == 2) {
                    ++this.stage;
                } else {
                    this.stage=0;
                }
                ++this.movements;
            } else if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).g() == AutoClickerL.ABORT_DESTROY_BLOCK) {
                if (this.stage == 3) {
                    ++this.failed;
                } else {
                    ++this.passed;
                }
                if (this.movements >= 200 && this.failed + this.passed > 60) {
                    double rat=(this.passed == 0) ? -1.0 : (this.failed / this.passed);
                    double vl=this.getVl();
                    if (rat > 2.5) {
                        if ((vl+=1.0 + (rat - 2.0) * 0.75) >= 4.0) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("RAT", rat),
                                    new AlertData("VL", vl)
                            };

                            this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
                        }
                    } else {
                        vl-=2.0;
                    }
                    this.setVl(vl);
                    this.movements=0;
                    this.passed=0;
                    this.failed=0;
                }
            }
        } else {
            this.stage=0;
        }
    }

}
