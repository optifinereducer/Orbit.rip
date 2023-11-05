package net.frozenorb.foxtrot.bounty;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyManager {

    @Getter
    private final Map<UUID, Bounty> bountyMap = new HashMap<>();

    public void save() {
        bountyMap.forEach((key, value) -> Foxtrot.getInstance().getGemMap().addGemsSync(value.getPlacedBy(), value.getGems()));
        bountyMap.clear();
    }

    public void placeBounty(Player player, Player target, int gems) {
        bountyMap.put(target.getUniqueId(), new Bounty(player.getUniqueId(), gems));
    }

    public Bounty getBounty(Player player) {
        return bountyMap.get(player.getUniqueId());
    }

    public Bounty removeBounty(Player player) {
        return bountyMap.remove(player.getUniqueId());
    }
}
