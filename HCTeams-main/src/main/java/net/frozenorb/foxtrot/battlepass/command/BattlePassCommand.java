package net.frozenorb.foxtrot.battlepass.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.battlepass.menu.BattlePassMenu;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class BattlePassCommand {

    @Command(names = { "battlepass", "bp", "missions", "challenges" }, description = "Opens the BattlePass menu", permission = "")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            return;
        }

        BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
        if (progress != null) {
            new BattlePassMenu(progress).openMenu(player);
        } else {
            player.sendMessage(CC.RED + "Couldn't open BattlePass!");
        }
    }

}
