package net.frozenorb.foxtrot.shop.blockshop.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.shop.blockshop.BlockShopHandler;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by vape on 10/30/2020 at 3:44 PM.
 */
@RequiredArgsConstructor
public class BlockShopMenu extends Menu {

    private static final Button GLASS = new GlassButton(7);
    private static final List<Integer> SLOTS = Stream.concat(IntStream.rangeClosed(10, 16).boxed(), Stream.concat(IntStream.rangeClosed(19, 25).boxed(), Stream.concat(IntStream.rangeClosed(28, 34).boxed(), IntStream.rangeClosed(37, 43).boxed()))).collect(Collectors.toList());

    private final Map<Integer, List<Button>> pageButtonMap = new HashMap<>();
    @Getter
    private int currentPage = 1, totalPages = 1;

    public BlockShopMenu(BlockShopHandler handler) {
        setAutoUpdate(true);

        int page = 0, i = 0;
        List<Button> buttons = new LinkedList<>();

        for (Map.Entry<ItemStack, Double> entry : handler.getPriceMap().entrySet()) {
            if (i++ > 27) {
                pageButtonMap.put(page++, new LinkedList<>(buttons));
                i = 1;
                buttons.clear();
            }

            buttons.add(new ItemButton(entry.getKey().clone(), entry.getValue()));
        }

        if (!buttons.isEmpty())
            pageButtonMap.put(page, buttons);

        totalPages = page + (buttons.isEmpty() ? 0 : 1);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Block Shop" + (totalPages > 1 ? (" - " + currentPage + "/" + totalPages) : "");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 54; i++) {
            buttons.put(i, GLASS);
        }

        if (totalPages > 1) {
            buttons.put(0, new PageButton(-1, this));
            buttons.put(8, new PageButton(1, this));
        }

        int i = 0;
        for (Button button : pageButtonMap.getOrDefault(currentPage - 1, new ArrayList<>())) {
            buttons.put(SLOTS.get(i++), button);
        }

        return buttons;
    }

    protected void changePage(int mod) {
        currentPage += mod;
    }
}