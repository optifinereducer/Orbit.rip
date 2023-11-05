package net.frozenorb.foxtrot.team.claims;

import org.bukkit.scheduler.*;
import org.bukkit.*;
import org.bukkit.block.*;

public class BlockRunnable extends BukkitRunnable
{
    private int maxPerTick;
    private int x;
    private int y;
    private int z;
    private int maxY;
    private int blocksPlaced;
    private Location min;
    private Location max;
    private Material material;
    private BlockOperation operation;

    public BlockRunnable(final BlockOperation operation, final Material material, final int minY, final Location min, final Location max, final int maxPerTick) {
        this.max = max;
        this.min = min;
        this.operation = operation;
        this.maxPerTick = maxPerTick;
        this.x = min.getBlockX();
        this.y = minY;
        this.z = min.getBlockZ();
        this.maxY = 125;
        this.material = material;
        System.out.println(this.y);
    }

    public void run() {
        Block block = null;
        this.blocksPlaced = 0;
        switch (this.operation) {
            case X: {
                while (this.y <= this.maxY) {
                    while (this.x <= this.max.getBlockX()) {
                        if (this.blocksPlaced >= this.maxPerTick) {
                            return;
                        }
                        block = new Location(this.min.getWorld(), (double)this.x, (double)this.y, (double)this.z).getBlock();
                        if (!block.getChunk().isLoaded()) {
                            block.getChunk().load();
                            return;
                        }
                        if (block.getType() != this.material) {
                            block.setType(this.material);
                            ++this.blocksPlaced;
                        }
                        block = new Location(this.min.getWorld(), (double)this.x, (double)this.y, (double)this.max.getBlockZ()).getBlock();
                        if (!block.getChunk().isLoaded()) {
                            block.getChunk().load();
                            return;
                        }
                        if (block.getType() != this.material) {
                            block.setType(this.material);
                            ++this.blocksPlaced;
                        }
                        ++this.x;
                    }
                    this.x = this.min.getBlockX();
                    ++this.y;
                }
                this.cancel();
                break;
            }
            case Z: {
                while (this.y <= this.maxY) {
                    while (this.z <= this.max.getBlockZ()) {
                        if (this.blocksPlaced >= this.maxPerTick) {
                            return;
                        }
                        block = new Location(this.min.getWorld(), (double)this.x, (double)this.y, (double)this.z).getBlock();
                        if (!block.getChunk().isLoaded()) {
                            block.getChunk().load();
                            return;
                        }
                        if (block.getType() != this.material) {
                            block.setType(this.material);
                            ++this.blocksPlaced;
                        }
                        block = new Location(this.min.getWorld(), (double)this.max.getBlockX(), (double)this.y, (double)this.z).getBlock();
                        if (!block.getChunk().isLoaded()) {
                            block.getChunk().load();
                            return;
                        }
                        if (block.getType() != this.material) {
                            block.setType(this.material);
                            ++this.blocksPlaced;
                        }
                        ++this.z;
                    }
                    this.z = this.min.getBlockZ();
                    if (this.y == this.maxY) {
                        for (int x = this.min.getBlockX(); x <= this.max.getBlockX(); ++x) {
                            for (int z = this.min.getBlockZ(); z <= this.max.getBlockZ(); ++z) {
                                block = this.min.getWorld().getBlockAt(x, this.y, z);
                                block.setType(this.material);
                            }
                        }
                    }
                    ++this.y;
                }
                this.cancel();
                break;
            }
        }
    }

    public enum BlockOperation
    {
        X("X", 0),
        Z("Z", 1);

        private BlockOperation(final String s, final int n) {
        }
    }
}