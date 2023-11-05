package net.frozenorb.foxtrot.gem.menu;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class GemMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GREEN + CC.BOLD + "Gem Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            buttons.put(i, new GlassButton(i % 2 == 0 ? 5 : 13));
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            buttons.put(11, new CrateKeyButton("Armory", "&7Armor", 20));
            buttons.put(12, new CrateKeyButton("Weaponry", "&6Weaponry", 25));
            buttons.put(13, new CrateKeyButton("Ability", "&dAbility", 10));
            buttons.put(14, new CrateKeyButton("CE", "&4&lCE", 5));
            buttons.put(15, new PartnerPackageButton());
        } else {
            buttons.put(11, new CrateKeyButton("Medieval", Foxtrot.getInstance().getMapHandler().isHalloween() ? "&cHalloween" : "&cMedieval", 15));
            buttons.put(12, new CrateKeyButton("Silver", "&7Silver", 3));
            buttons.put(13, new CrateKeyButton("Gold", "&6Gold", 5));
            buttons.put(14, new CrateKeyButton("Diamond", "&bDiamond", 7));
            buttons.put(15, new PartnerPackageButton());
            buttons.put(21, new GKitButton("&f&lMiner Kit", "miner", Material.IRON_HELMET, 35));
            buttons.put(22, new GKitButton("&e&lBard Kit", "bard", Material.GOLD_HELMET, 30));
            buttons.put(23, new GKitButton("&b&lDiamond Kit", "diamond", Material.DIAMOND_HELMET, 40));
            ItemStack life = ItemBuilder.of(Material.PAPER)
                    .name("&a&lONE &aLife")
                    .addToLore(CC.translate("&7Purchase a life!"),
                            " ",
                            CC.DARK_GREEN + "Gems: " + CC.GREEN + 3
                    ).build();

            buttons.put(31, new CommandButton(life, "pvp addlives %player% 1", $ -> true, 3));
        }

        return buttons;
    }

    @AllArgsConstructor
    private static class GKitButton extends Button {

        private final String displayName;
        private final String kitName;
        private final Material material;
        private final int cost;

        @Override
        public String getName(Player player) {
            return ChatColor.translateAlternateColorCodes('&', displayName);
        }

        @Override
        public Material getMaterial(Player player) {
            return material;
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    CC.translate("&7A one time use of the " + displayName + "&7."),
                    " ",
                    CC.DARK_GREEN + "Gems: " + CC.GREEN + cost
            );
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.RED + "You do not have enough inventory space for this!");
                return;
            }

            if (Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), cost)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gkitz " + kitName + " " + player.getName());
            } else {
                player.sendMessage(CC.RED + "You do not have enough gems for this!");
            }
        }

    }

    @RequiredArgsConstructor
    private static class CrateKeyButton extends Button {

        private final String key;
        private final String name;
        private final int cost;

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.RED + "You do not have enough inventory space for this!");
                return;
            }

            if (Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), cost)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " " + key + " 1");
            } else {
                player.sendMessage(CC.RED + "You do not have enough gems for this!");
            }
        }

        @Override
        public String getName(Player player) {
            return CC.BOLD + CC.translate(name + " Key");
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    CC.GRAY + "Right click the " + ChatColor.translateAlternateColorCodes('&', name) + " Crate " + CC.GRAY + "to obtain rewards!",
                    "",
                    CC.DARK_GREEN + "Gems: " + CC.GREEN + cost
            );
        }

        @Override
        public Material getMaterial(Player player) {
            if (key.equals("Medieval")) {
                return Material.GOLD_NUGGET;
            }
            return Material.TRIPWIRE_HOOK;
        }
    }

    private static class PartnerPackageButton extends Button {

        private final int requiredGems = Foxtrot.getInstance().getMapHandler().isKitMap() ? 10 : 15;

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.copyOf(Foxtrot.getInstance().getPartnerCrateHandler().getCrateItem().clone())
                    .addToLore("")
                    .addToLore(CC.DARK_GREEN + "Gems: " + CC.GREEN + requiredGems)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.RED + "You do not have enough inventory space for this!");
                return;
            }

            if (Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), requiredGems)) {
                InventoryUtils.addAmountToInventory(player.getInventory(), Foxtrot.getInstance().getPartnerCrateHandler().getCrateItem(), 1);
                player.sendMessage(CC.GREEN + "Successfully purchased one partner package!");
            } else {
                player.sendMessage(CC.RED + "You do not have enough gems for this!");
            }
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
    }

    @RequiredArgsConstructor
    private static class CommandButton extends Button {

        private final ItemStack itemStack;
        private final String command;
        private final Predicate<Player> predicate;
        private final int cost;

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick() && predicate.test(player)) {
                player.closeInventory();
                if (Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), cost)) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
//							player.sendMessage("§aPurchased " + itemStack.getItemMeta().getDisplayName() + "§a!");
                } else {
                    player.sendMessage(CC.RED + "You do not have enough gems for this!");
                }
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return itemStack;
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
    }

    @RequiredArgsConstructor
    private static class GlassButton extends Button {

        private final int glassData;

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.of(Material.STAINED_GLASS_PANE)
                    .name(" ")
                    .data((short) glassData)
                    .build();
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
    }
}
