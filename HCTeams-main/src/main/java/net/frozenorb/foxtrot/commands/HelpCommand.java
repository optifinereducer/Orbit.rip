package net.frozenorb.foxtrot.commands;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.qlib.command.Command;

public class HelpCommand {

    @Command(names={ "Help" }, permission="")
    public static void help(Player sender) {
        String sharp = "Sharpness " + Enchantment.DAMAGE_ALL.getMaxLevel();
        String prot = "Protection " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        String bow = "Power " + Enchantment.ARROW_DAMAGE.getMaxLevel();

        String serverName = Foxtrot.getInstance().getServerHandler().getServerName();
        String serverWebsite = Foxtrot.getInstance().getServerHandler().getNetworkWebsite();

        sender.sendMessage(new String[] {

                "§7§m-----------------------------------------------------",
                "§bHelpful Commands:",
                "§f/report <player> <reason> §f- Report rule breakers.",
                "§f/request <message> §f- Request staff assistance.",
                "§f/settings §f- Customize your gameplay.",
                "§f/tgc §f- Toggle chat visibility.",
                "§f/tpm §f- Toggle private messaging.",

                "",
                "§bUseful Links:",
                "§fTeamSpeak §f- ts.warzone.rip",
                "§fDiscord §f- warzone.rip/discord",
                "§fStore §f- store.warzone.rip",
                "§8§m-----------------------------------------------------",

        });
    }

}
