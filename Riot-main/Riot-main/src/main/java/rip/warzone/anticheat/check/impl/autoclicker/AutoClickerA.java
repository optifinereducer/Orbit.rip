package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class AutoClickerA extends PacketCheck {

    private int swings;
    private int movements;

    public AutoClickerA(PlayerData playerData) {
        super(playerData, "AutoClicker #1");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInArmAnimation && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
            ++this.swings;
        } else if (packet instanceof PacketPlayInFlying && this.swings > 0 && ++this.movements == 20) {
            AlertData[] alertData=new AlertData[]
                    {new AlertData("CPS",this.swings)};

            if (this.swings > 20 && this.alert(player, AlertType.RELEASE, alertData, true)) {
                int violations=this.playerData.getViolations(this, 60000L);

                if (!this.playerData.isBanning() && violations > 15) {
                    this.ban(player);
                }
            }
            int n=0;
            this.swings=n;
            this.movements=n;
        }
        playerData.setLastCps(this.swings);
    }
}
