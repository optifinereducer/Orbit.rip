package rip.warzone.anticheat.check;

import org.bukkit.entity.Player;

public interface ICheck<T> {

    void handleCheck(Player player, T type);

    Class<? extends T> getType();

}
