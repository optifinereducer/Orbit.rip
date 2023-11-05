package net.frozenorb.foxtrot.server.broadcast;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ServerBroadcastTask extends BukkitRunnable {

    private static final List<ServerBroadcast> BROADCASTS = Arrays.asList(
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Teamspeak",
                    "Need staff assistance? Join our ",
                    "teamspeak for 24/7 support!",
                    "ts.warzone.rip"
            }),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Discord",
                    "Join our discord to stay updated",
                    "with Warzone and enter giveaways!",
                    "warzone.rip/discord"
            }),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Store",
                    "Support us by making a purchase on our store!",
                    "store.warzone.rip"
            }),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Gems",
                    "Gems can be obtained through killing enemies",
                    "and mining! Use gems to purchase keys,",
                    "lives and one-time use gkits!",
                    "/gem help"
            }),
            new ServerBroadcast(ServerBroadcast.Type.HCF_ONLY, new String[]{
                    "Battle Pass",
                    "Complete challenges to rank up your",
                    "battle pass tiers and claim rewards!",
                    "/battlepass"
            }),
            new ServerBroadcast(ServerBroadcast.Type.HCF_ONLY, new String[]{
                    "Premium Battle Pass",
                    "Purchase the premium battle pass to",
                    "gain access to premium challenges",
                    "and amazing rewards!",
                    "store.warzone.rip"
            })
    );

    private static final List<ServerBroadcast> ACTIVE_BROADCASTS = new ArrayList<>();

    static {
        for (ServerBroadcast broadcast : BROADCASTS) {
            if (broadcast.isActive())
                ACTIVE_BROADCASTS.add(broadcast);
        }
    }

    private int index = -1;

    @Override
    public void run() {
        index++;

        if (index > ACTIVE_BROADCASTS.size() - 1) {
            index = 0;
        }

        ServerBroadcast broadcast = ACTIVE_BROADCASTS.get(index);
        if (broadcast == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(" ");
            player.sendMessage(broadcast.getMessage());
            player.sendMessage(" ");
        }
    }

}
