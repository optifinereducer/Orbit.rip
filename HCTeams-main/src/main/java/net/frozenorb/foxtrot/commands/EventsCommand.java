package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.GlowUtil;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventsCommand {

	@Command(names = "events help", permission = "")
	public static void debug(Player sender) {
		new EventsMenu().openMenu(sender);
	}

	private static class EventsMenu extends Menu {

		@Override
		public String getTitle(Player player) {
			return CC.PINK + "Events Help";
		}

		@Override
		public Map<Integer, Button> getButtons(Player player) {
			Map<Integer, Button> buttons = new HashMap<>();


			for (int i = 0; i < 27; i++) {
				if (i > 10 && i < 16) continue;
				buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 8));
			}

			ItemStack koth = ItemBuilder.of(Material.CHEST)
					.name("&6&lKOTH Events")
					.addToLore(
							"&8&m---------------------------------------------------—",
							"&eWhen?",
							"&f/koth schedule (Every 1h 30m)",
							" ",
							"&eWhat is it?",
							"&fStand inside the capture zone without",
							"&fgetting knocked outside and receive",
							"&fa KOTH Key that has OP Loot!",
							" ",
							"&e&oTip: &7&oPartner Items and GKits cannot be used in KOTH regions!",
							"&8&m---------------------------------------------------—"
					).build();

			buttons.put(11, Button.fromItem(koth));

			ItemStack gem = ItemBuilder.of(Material.EMERALD)
					.name("&2&lDouble Gem Event")
					.enchant(Enchantment.DURABILITY, 1)
					.addToLore(

							"&8&m---------------------------------------------------—",
							"&aWhen?",
							"&fThursday, 4-5 PM EST",
							" ",
							"&aWhat is it?",
							"&fDuring this event, all gems received will",
							"&fbe multiplied by 2.",
							" ",
							"&a&oTip: &7&oYou cannot use enderpearls inside of Citadel!",
							"&8&m---------------------------------------------------—"
					).build();

			buttons.put(12, Button.fromItem(gem));

			ItemStack purge = ItemBuilder.of(Material.INK_SACK)
					.name("&4&lPurge Event")
					.data((short) 1)
					.enchant(Enchantment.DURABILITY, 1)
					.addToLore(
							"&8&m---------------------------------------------------—",
							"&cWhen?",
							"&fMonday & Wednesday, 4-5 PM EST",
							" ",
							"&cWhat is it?",
							"&fDuring the purge, players can open",
							"&ffencegates, doors, trapdoors, and",
							"&fsteal loot in enemy claims!",
							" ",
							"&c&oTip: &7&oBlock up your base to avoid enemies from stealing loot!",
							"&8&m---------------------------------------------------—"
					).build();

			buttons.put(13, Button.fromItem(purge));

			ItemStack citadel = ItemBuilder.of(Material.BEACON)
					.name("&5&lCitadel Event")
					.addToLore(
							"&8&m---------------------------------------------------—",
							"&6When?",
							"&fSunday, 3 PM EST",
							" ",
							"&6What is it?",
							"&fA special KOTH that when captured,",
							"&fit grants you access to special loot",
							"&fthat respawns every 2 hours!",
							" ",
							"&6&oTip: &7&oYou cannot use enderpearls inside of Citadel!",
							"&8&m---------------------------------------------------—"
					).build();

			buttons.put(14, Button.fromItem(citadel));

			ItemStack partner = ItemBuilder.of(Material.ENDER_CHEST)
					.name("&5&lPartner Package Event")
					.addToLore(
							"&8&m---------------------------------------------------—",
							"&dWhen?",
							"&fTuesday, 4-5 PM EST",
							" ",
							"&dWhat is it?",
							"&fDuring this event, all partner items are",
							"&fbuffed and cooldowns are reduced.",
							"",
							"&d&oTip: &7&oTo see all partner items, type /partneritems!",
							"&8&m---------------------------------------------------—"

					).build();

			buttons.put(15, Button.fromItem(partner));

			return buttons;
		}
	}

	private static class EventButton extends Button {

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
	}

}
