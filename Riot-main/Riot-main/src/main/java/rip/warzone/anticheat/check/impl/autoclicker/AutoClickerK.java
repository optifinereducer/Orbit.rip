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

public class AutoClickerK extends PacketCheck {

    private int stage;
    private boolean other;

    public AutoClickerK(PlayerData playerData) {
        super(playerData, "AutoClicker #11");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (this.stage == 0) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).g() == AutoClickerK.START_DESTROY_BLOCK) {
                ++this.stage;
            }
        } else if (this.stage == 1) {
            if (packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (this.stage == 2) {
            if (packet instanceof PacketPlayInFlying) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (this.stage == 3) {
            if (packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (this.stage == 4) {
            if (packet instanceof PacketPlayInFlying) {
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (this.stage == 5) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).g() == AutoClickerK.START_DESTROY_BLOCK) {
                if (this.alert(player, AlertType.EXPERIMENTAL, new AlertData[0], false)) {
                    this.checkBan(player);
                }
                this.stage=0;
            } else if (packet instanceof PacketPlayInArmAnimation) {
                ++this.stage;
            } else if (packet instanceof PacketPlayInFlying) {
                this.other=true;
                ++this.stage;
            } else {
                this.stage=0;
            }
        } else if (this.stage == 6) {
            if (!this.other) {
                if (packet instanceof PacketPlayInFlying) {
                    ++this.stage;
                } else {
                    this.stage=0;
                }
            } else {
                if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).g() == AutoClickerK.START_DESTROY_BLOCK) {
                    AlertData[] alertData=new AlertData[]{
                            new AlertData("Type", "B")
                    };

                    if (this.alert(player, AlertType.EXPERIMENTAL, alertData, false)) {
                        this.checkBan(player);
                    }

                    this.other=false;
                }
                this.stage=0;
            }
        } else if (this.stage == 7) {
            if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).g() == AutoClickerK.START_DESTROY_BLOCK) {
                AlertData[] alertData=new AlertData[]{
                        new AlertData("Type", "C")
                };

                if (this.alert(player, AlertType.EXPERIMENTAL, alertData, false)) {
                    this.checkBan(player);
                }
            } else {
                this.stage=0;
            }
        }
    }

    private void checkBan(Player player) {
        int violations=this.playerData.getViolations(this, 60000L);

        if (!this.playerData.isBanning() && violations > 2) {
            this.ban(player);
        }
    }

}
