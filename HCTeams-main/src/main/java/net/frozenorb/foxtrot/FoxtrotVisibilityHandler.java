package net.frozenorb.foxtrot;

import net.frozenorb.foxtrot.util.modsuite.ModUtils;
import net.frozenorb.qlib.visibility.VisibilityAction;
import net.frozenorb.qlib.visibility.VisibilityHandler;
import org.bukkit.entity.Player;

public class FoxtrotVisibilityHandler implements VisibilityHandler {

    @Override
    public VisibilityAction getAction(Player player, Player viewer) {
        if (ModUtils.isInvisible(player)) {
            // always hide player if the viewer is hiding staff
            if (ModUtils.hideStaff.contains(viewer.getUniqueId())) {
                return VisibilityAction.HIDE;
            }

            // show player if viewer is also in mod-mode
            if (ModUtils.isModMode(viewer)) {
                return VisibilityAction.NEUTRAL;
            }

            // otherwise hide
            return VisibilityAction.HIDE;
        }

        return VisibilityAction.NEUTRAL;
    }

}
