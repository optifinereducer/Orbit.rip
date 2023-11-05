package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

import java.util.Collection;
import java.util.LinkedList;
import java.util.OptionalDouble;
import java.util.Queue;

public class AutoClickerM extends PacketCheck {

    private final Queue<Integer> flyingPackets;
    private int currentCount;

    public AutoClickerM(PlayerData playerData) {
        super(playerData, "Auto Clicker #13");
        this.flyingPackets=new LinkedList<>();
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (packet instanceof PacketPlayInFlying) {
            ++this.currentCount;
        } else if (packet instanceof PacketPlayInArmAnimation) {
            if (this.playerData.isDigging() || this.playerData.isPlacing()) {
                return;
            }
            if (this.currentCount >= 10) {
                this.currentCount=0;
                return;
            }
            this.flyingPackets.add(this.currentCount);
            if (this.flyingPackets.size() >= 75) {
                this.handleFlyingPackets(player);
                this.flyingPackets.clear();
            }
            this.currentCount=0;
        }
    }

    private void handleFlyingPackets(Player player) {
        double rangeDifference=this.getRangeDifference(this.flyingPackets);
        if (rangeDifference < 2.0) {
            AlertData[] alertData=new AlertData[]
                    {new AlertData(String.format("RD=%s", rangeDifference))};

            this.alert(player, AlertType.RELEASE, alertData, false);
        }
    }

    private double getRangeDifference(Collection<? extends Number> numbers) {
        OptionalDouble minOptional=numbers.stream().mapToDouble(Number::doubleValue).min();
        OptionalDouble maxOptional=numbers.stream().mapToDouble(Number::doubleValue).max();
        if (!minOptional.isPresent() || !maxOptional.isPresent()) {
            return 500.0;
        }
        return maxOptional.getAsDouble() - minOptional.getAsDouble();
    }
}
