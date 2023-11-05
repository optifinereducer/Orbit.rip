package rip.warzone.anticheat.check.impl.killaura;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class KillAuraT extends PacketCheck {

    private int lastTick;

    public KillAuraT(PlayerData playerData) {
        super(playerData, "KillAura #20");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        double vl=this.getVl();
        if (packet instanceof PacketPlayInUseEntity) {
            if (playerData.currentTick == lastTick) {
                AlertData[] data=new AlertData[]{
                        new AlertData("Tick:",playerData.currentTick)
                };
                if (vl++ > 0) alert(player, AlertType.RELEASE, data, true);
            } else vl=Math.max(0, vl - 1);
        } else if (packet instanceof PacketPlayInBlockDig) {
            lastTick=playerData.currentTick;
        }
        this.setVl(vl);
    }
}
