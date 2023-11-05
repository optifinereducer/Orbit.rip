package rip.warzone.anticheat.check.impl.autoclicker;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.alert.AlertData;
import rip.warzone.anticheat.check.checks.PacketCheck;
import rip.warzone.anticheat.event.AlertType;
import rip.warzone.anticheat.player.PlayerData;

import java.util.ArrayList;

public class AutoClickerP extends PacketCheck {

    private int clicks, verbose;
    private final ArrayList<Integer> av;
    private final ArrayList<Double> av2;
    private final ArrayList<Double> patterns;

    public AutoClickerP(PlayerData playerData) {
        super(playerData, "AutoClicker #16");
        av=new ArrayList<>();
        av2=new ArrayList<>();
        patterns=new ArrayList<>();
    }

    @Override
    public void handleCheck(Player player, Packet type) {
        if (type instanceof PacketPlayInArmAnimation) {
            clicks++;
        } else if (type instanceof PacketPlayInBlockDig) {
            verbose=0;
            clicks=Math.max(0, clicks - 1);
        } else if (type instanceof PacketPlayInFlying) {
            if (playerData.currentTick % 20 == 0) {
                av.add(clicks);
                if (av.size() > 7) {
                    av.remove(0);
                }

                if (av.size() >= 7) {
                    int added=0;
                    for ( int i=0; i < av.size() - 1; i++ ) {
                        added+=av.get(i);
                    }
                    double value=added / av.size();
                    for ( int i=0; i < av.size() - 1; i++ ) {
                        av2.add((av.get(i) - value) * (av.get(i) - value));
                    }
                    double value2=0;
                    for ( int i=0; i < av2.size() - 1; i++ ) {
                        value2+=av2.get(i);
                    }
                    double value3=0.16666667;
                    double next=Math.sqrt(value2 * value3);

                    if (patterns.contains(next) && clicks > 5 && next > 1.05) {
                        if (verbose++ > 3) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("V", next),
                                    new AlertData("C", clicks),
                                    new AlertData("Verbose", verbose)
                            };
                            this.alert(player, AlertType.RELEASE, alertData, true);
                            if (verbose > 5) {
                                AntiCheat.instance.getBanWaveManager().addToBanWithChecking(player.getUniqueId());
                            }
                        }
                    } else verbose=Math.max(0, verbose - 1);

                    av2.clear();
                    patterns.add(next);
                    if (patterns.size() > 5) {
                        patterns.remove(0);
                    }
                }
                clicks=0;
            }
        }
    }


}
