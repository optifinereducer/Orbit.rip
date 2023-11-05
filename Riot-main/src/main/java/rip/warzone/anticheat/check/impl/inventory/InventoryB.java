package rip.warzone.anticheat.check.impl.inventory;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInEntityAction;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class InventoryB extends PacketCheck {

    public InventoryB(PlayerData playerData) {
        super(playerData, "Inventory #2");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (((packet instanceof PacketPlayInEntityAction && ((PacketPlayInEntityAction) packet).d() == InventoryB.START_SPRINTING) || packet instanceof PacketPlayInArmAnimation) && this.playerData.isInventoryOpen()) {
            if (this.alert(player, AlertType.RELEASE, new AlertData[0], true)) {
                int violations=this.playerData.getViolations(this, 60000L);
                if (!this.playerData.isBanning() && violations > 5) {
                    this.ban(player);
                }
            }
            this.playerData.setInventoryOpen(false);
        }
    }

}
