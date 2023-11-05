package net.frozenorb.foxtrot.partner;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PartnerPackageMenu extends Menu {

	private final boolean purge;

	public PartnerPackageMenu(boolean purge) {
		this.purge = purge;
	}

	@Override
	public String getTitle(Player player) {
		return purge ? ChatColor.GRAY + "All purge items" : ChatColor.GRAY + "All partner items";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		PartnerPackageHandler handler = Foxtrot.getInstance().getPartnerPackageHandler();
		int slot = 0;
		for (int i = 0; i < handler.getPackages().size(); i++) {
			PartnerPackage partnerPackage = handler.getPackages().get(i);
			if (this.purge && !partnerPackage.isPurge()) continue;
			if (!this.purge && partnerPackage.isPurge()) continue;
			buttons.put(slot++, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					return partnerPackage.getPartnerItem();
				}

				@Override
				public String getName(Player player) {
					return null;
				}

				@Override
				public List<String> getDescription(Player player) {
					return null;
				}

				@Override
				public Material getMaterial(Player player) {
					return null;
				}

				@Override
				public void clicked(Player player, int slot, ClickType clickType) {
					if (clickType.isRightClick() && player.isOp()) {
						player.closeInventory();
						InventoryUtils.addAmountToInventory(player.getInventory(), partnerPackage.getPartnerItem(), 6);
					}
				}
			});
		}
		return buttons;
	}

}
