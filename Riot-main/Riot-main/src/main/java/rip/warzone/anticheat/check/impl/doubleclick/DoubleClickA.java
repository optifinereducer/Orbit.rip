package rip.warzone.anticheat.check.impl.doubleclick;

import net.minecraft.server.v1_7_R4.EnumEntityUseAction;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

public class DoubleClickA extends PacketCheck {

    public DoubleClickA(PlayerData playerData) {
        super(playerData, "DoubleClick #1");
    }

    private int lastHitTick;

    //Made to flag butterfly more accurately but can also detect Tick auras

    @Override
    public void handleCheck(Player player, Packet type) {
        if (type instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) type).c() == EnumEntityUseAction.ATTACK) {
            if (playerData.currentTick == lastHitTick) {
                alert(player, AlertType.EXPERIMENTAL, new AlertData[]{new AlertData("currentTick", playerData.currentTick)}, false);
            }
            lastHitTick=playerData.currentTick;
        }
    }
}
