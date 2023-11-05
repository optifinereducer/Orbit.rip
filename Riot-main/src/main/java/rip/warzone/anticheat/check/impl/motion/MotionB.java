package rip.warzone.anticheat.check.impl.motion;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;
import rip.warzone.anticheat.util.MathUtil;
import rip.warzone.anticheat.util.Verbose;

public class MotionB extends PacketCheck {

    private int hits;
    private int value;
    private final Verbose verbose=new Verbose();

    public MotionB(PlayerData playerData) {
        super(playerData, "Motion #2");
    }

    @Override
    public void handleCheck(Player player, Packet type) {
        if (type instanceof PacketPlayInFlying) {
            if (System.currentTimeMillis() - playerData.getLastAttack() < 150) {
                double speed=playerData.getMovementSpeed();
                if (speed > 0) {
                    if (hits > 0) {
                        value++;
                    }
                    double max=value > 2 && playerData.isSprinting() ? getBaseSpeed(player, 0.281f) : getBaseSpeed(player, 0.282f);
                    if (speed > max && hits > 0) {
                        if (verbose.flag(4, 600)) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("S", speed),
                                    new AlertData("M", max),
                                    new AlertData("H", hits)
                            };

                            alert(player, AlertType.RELEASE, alertData, true);
                        }
                    }
                } else {
                    value=0;
                }

            } else {
                value=0;
            }

            hits=0;
        } else if (type instanceof PacketPlayInUseEntity) {
            hits++;
            playerData.setLastAttack(System.currentTimeMillis());
        }
    }

    private float getBaseSpeed(Player player, float normal) {
        return normal + (MathUtil.getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.059f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

}
