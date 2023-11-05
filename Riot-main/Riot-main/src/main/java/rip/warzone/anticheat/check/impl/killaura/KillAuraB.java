package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraB extends PacketCheck {

    private boolean sent;
    private boolean failed;
    private int movements;

    public KillAuraB(PlayerData playerData) {
        super(playerData, "KillAura #2");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (this.playerData.isDigging() && System.currentTimeMillis() - this.playerData.getLastDelayedMovePacket() > 220L && this.playerData.getLastMovePacket() != null &&
                System.currentTimeMillis() - this.playerData.getLastMovePacket().getTimestamp() < 110L && !this.playerData.isInstantBreakDigging()) {
            int vl=(int) this.getVl();
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).g() == KillAuraB.START_DESTROY_BLOCK) {
                this.movements=0;
                vl=0;
            } else if (packet instanceof PacketPlayInArmAnimation && this.movements >= 2) {
                if (this.sent) {
                    if (!this.failed) {
                        if (++vl >= 5) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("VL", vl)
                            };

                            this.alert(player, AlertType.EXPERIMENTAL, alertData, false);
                        }
                        this.failed=true;
                    }
                } else {
                    this.sent=true;
                }
            } else if (packet instanceof PacketPlayInFlying) {
                boolean b=false;
                this.failed=false;
                this.sent=false;
                ++this.movements;
            }
            this.setVl(vl);
        }
    }
}
