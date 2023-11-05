package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.ClaimBuildMenu;
import org.bukkit.entity.*;
import net.frozenorb.foxtrot.team.menu.*;
import net.frozenorb.qlib.command.*;

public class TeamBuildCommand
{
    @Command(names = { "team build", "t build", "f build", "fac build", "faction build" }, permission = "")
    public static void captainAdd(final Player sender) {
        new ClaimBuildMenu().openMenu(sender);
    }
}
