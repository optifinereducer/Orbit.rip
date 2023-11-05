package rip.warzone.hub.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.joeleoli.portal.shared.queue.Queue;
import net.frozenorb.hydrogen.Hydrogen;
import org.bukkit.Material;

import java.util.List;

@Data
@AllArgsConstructor
public class Server {

    private final String name;
    private int guiSlot;
    private final Material icon;
    private final String displayName;
    private final List<String> description;

    public Queue getQueue(){
        return Queue.getByName(name);
    }

}
