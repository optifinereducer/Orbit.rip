package net.frozenorb.foxtrot.shop.blockshop;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.shop.blockshop.commands.BlockShopAdminCommands;
import net.frozenorb.foxtrot.shop.blockshop.commands.BlockShopCommand;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by vape on 10/30/2020 at 3:20 PM.
 */
public class BlockShopHandler {

    @Getter private final Map<ItemStack, Double> priceMap = new LinkedHashMap<>();
    @Setter @Getter private boolean enabled = true;

    public BlockShopHandler() {
        FrozenCommandHandler.registerClass(BlockShopCommand.class);
        FrozenCommandHandler.registerClass(BlockShopAdminCommands.class);

//        priceMap.put(ItemBuilder.of(Material.YELLOW_FLOWER)
//                             .name(ChatColor.LIGHT_PURPLE + "Testing Flower")
//                             .setLore(Arrays.asList(ChatColor.GRAY + "haha lol", ChatColor.GRAY + "Rowin is coder"))
//                             .build(), 100.0);

        add(Material.STONE);
        add(Material.GRASS);
        add(Material.DIRT);
        add(Material.COBBLESTONE);
        add(Material.WOOD, 0, 5);
        add(Material.SAND);
        add(Material.SAND, 1);
        add(Material.GRAVEL);
        add(Material.LOG);
        add(Material.LOG, 1);
        add(Material.LOG, 2);
        add(Material.LOG, 3);
        add(Material.SPONGE);
        add(Material.GLASS);
        add(Material.WOOL, 0, 15);
        add(Material.STEP, 0, 7, Collections.singletonList(2));
        add(Material.BRICK);
        add(Material.BOOKSHELF);
        add(Material.STAINED_GLASS, 0, 15);
        add(Material.SMOOTH_BRICK, 0, 3);
        add(Material.WOOD_STEP, 0, 5);
        add(Material.STAINED_CLAY, 0, 15);
        add(Material.SAPLING, 0, 5);
    }

    private void add(Material material) {
        add(material, 0);
    }

    @SuppressWarnings("ALL")
    private void add(Material material, int start, int end) {
        add(material, start, end, Collections.emptyList());
    }

    private void add(Material material, int start, int end, List<Integer> exclude) {
        for (int i = start; i <= end; i++) {
            if (exclude.contains(i)) continue;
            add(material, i);
        }
    }

    private void add(Material material, int data) {
        priceMap.put(ItemBuilder.of(material).amount(64).data((short) data).build(), 500.0);
    }

}