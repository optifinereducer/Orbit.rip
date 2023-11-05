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

public class AutoClickerR extends PacketCheck {

    private int clicks, verbose;
    private final ArrayList<Integer> av;
    private final ArrayList<Double> av2;
    private final ArrayList<Double> patterns;

    public AutoClickerR(PlayerData playerData) {
        super(playerData, "AutoClicker #18");
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
            if (playerData.currentTick % 5 == 0) {
                av.add(clicks);
                if (av.size() > 3) {
                    av.remove(0);
                }

                if (av.size() >= 3) {
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
                    double next=Math.sqrt(value2 * (clicks * value2 * (0.24)));

                    if (clicks > 1 && patterns.contains(next) && next > 0.7 && next < 1 && !this.playerData.isDigging() && !this.playerData.isPlacing()) {
                        if (verbose++ > 3) {
                            AlertData[] alertData=new AlertData[]{
                                    new AlertData("V",next),
                                    new AlertData("C", clicks),
                                    new AlertData("Verbose", verbose)
                            };
                            this.alert(player, AlertType.EXPERIMENTAL, alertData, true);
                        }

                        if (verbose > 6) {
                            AntiCheat.instance.getBanWaveManager().addToBanWithChecking(player.getUniqueId());
                        }
                    } else verbose-=verbose > 0 ? 1 : 0;

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