package rip.warzone.hub.tab;

import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import org.bukkit.entity.Player;

public class HubTabLayout implements LayoutProvider {

    @Override
    public TabLayout provide(Player player) {
        TabLayout tabLayout = TabLayout.create(player);
        tabLayout.set(0, 1, "&c&lWarzone &7Network");
        tabLayout.set(3, 0, "&c&lStore");
        tabLayout.set(4, 0, "&7store." + Settings.getNetworkWebsite());
        return tabLayout;
    }
}
