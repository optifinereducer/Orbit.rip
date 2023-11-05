package rip.warzone.anticheat.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import rip.warzone.anticheat.AntiCheat;
import rip.warzone.anticheat.player.PlayerData;

import java.util.HashSet;
import java.util.Set;

public class BlockUtil {

    private static Set<Byte> blockSolidPassSet;
    private static Set<Byte> blockStairsSet;
    private static Set<Byte> blockLiquidsSet;
    private static Set<Byte> blockWebsSet;
    private static Set<Byte> blockIceSet;
    private static Set<Byte> blockCarpetSet;

    static {
        BlockUtil.blockSolidPassSet=new HashSet<>();
        BlockUtil.blockStairsSet=new HashSet<>();
        BlockUtil.blockLiquidsSet=new HashSet<>();
        BlockUtil.blockWebsSet=new HashSet<>();
        BlockUtil.blockIceSet=new HashSet<>();
        BlockUtil.blockCarpetSet=new HashSet<>();
        BlockUtil.blockSolidPassSet.add((byte) 0);
        BlockUtil.blockSolidPassSet.add((byte) 6);
        BlockUtil.blockSolidPassSet.add((byte) 8);
        BlockUtil.blockSolidPassSet.add((byte) 9);
        BlockUtil.blockSolidPassSet.add((byte) 10);
        BlockUtil.blockSolidPassSet.add((byte) 11);
        BlockUtil.blockSolidPassSet.add((byte) 27);
        BlockUtil.blockSolidPassSet.add((byte) 28);
        BlockUtil.blockSolidPassSet.add((byte) 30);
        BlockUtil.blockSolidPassSet.add((byte) 31);
        BlockUtil.blockSolidPassSet.add((byte) 32);
        BlockUtil.blockSolidPassSet.add((byte) 37);
        BlockUtil.blockSolidPassSet.add((byte) 38);
        BlockUtil.blockSolidPassSet.add((byte) 39);
        BlockUtil.blockSolidPassSet.add((byte) 40);
        BlockUtil.blockSolidPassSet.add((byte) 50);
        BlockUtil.blockSolidPassSet.add((byte) 51);
        BlockUtil.blockSolidPassSet.add((byte) 55);
        BlockUtil.blockSolidPassSet.add((byte) 59);
        BlockUtil.blockSolidPassSet.add((byte) 63);
        BlockUtil.blockSolidPassSet.add((byte) 66);
        BlockUtil.blockSolidPassSet.add((byte) 68);
        BlockUtil.blockSolidPassSet.add((byte) 69);
        BlockUtil.blockSolidPassSet.add((byte) 70);
        BlockUtil.blockSolidPassSet.add((byte) 72);
        BlockUtil.blockSolidPassSet.add((byte) 75);
        BlockUtil.blockSolidPassSet.add((byte) 76);
        BlockUtil.blockSolidPassSet.add((byte) 77);
        BlockUtil.blockSolidPassSet.add((byte) 78);
        BlockUtil.blockSolidPassSet.add((byte) 83);
        BlockUtil.blockSolidPassSet.add((byte) 90);
        BlockUtil.blockSolidPassSet.add((byte) 104);
        BlockUtil.blockSolidPassSet.add((byte) 105);
        BlockUtil.blockSolidPassSet.add((byte) 115);
        BlockUtil.blockSolidPassSet.add((byte) 119);
        BlockUtil.blockSolidPassSet.add((byte) (-124));
        BlockUtil.blockSolidPassSet.add((byte) (-113));
        BlockUtil.blockSolidPassSet.add((byte) (-81));
        BlockUtil.blockStairsSet.add((byte) 53);
        BlockUtil.blockStairsSet.add((byte) 67);
        BlockUtil.blockStairsSet.add((byte) 108);
        BlockUtil.blockStairsSet.add((byte) 109);
        BlockUtil.blockStairsSet.add((byte) 114);
        BlockUtil.blockStairsSet.add((byte) (-128));
        BlockUtil.blockStairsSet.add((byte) (-122));
        BlockUtil.blockStairsSet.add((byte) (-121));
        BlockUtil.blockStairsSet.add((byte) (-120));
        BlockUtil.blockStairsSet.add((byte) (-100));
        BlockUtil.blockStairsSet.add((byte) (-93));
        BlockUtil.blockStairsSet.add((byte) (-92));
        BlockUtil.blockStairsSet.add((byte) (-76));
        BlockUtil.blockStairsSet.add((byte) 126);
        BlockUtil.blockStairsSet.add((byte) (-74));
        BlockUtil.blockStairsSet.add((byte) 44);
        BlockUtil.blockStairsSet.add((byte) 78);
        BlockUtil.blockStairsSet.add((byte) 99);
        BlockUtil.blockStairsSet.add((byte) (-112));
        BlockUtil.blockStairsSet.add((byte) (-115));
        BlockUtil.blockStairsSet.add((byte) (-116));
        BlockUtil.blockStairsSet.add((byte) (-105));
        BlockUtil.blockStairsSet.add((byte) (-108));
        BlockUtil.blockStairsSet.add((byte) 100);
        BlockUtil.blockLiquidsSet.add((byte) 8);
        BlockUtil.blockLiquidsSet.add((byte) 9);
        BlockUtil.blockLiquidsSet.add((byte) 10);
        BlockUtil.blockLiquidsSet.add((byte) 11);
        BlockUtil.blockWebsSet.add((byte) 30);
        BlockUtil.blockIceSet.add((byte) 79);
        BlockUtil.blockIceSet.add((byte) (-82));
        BlockUtil.blockCarpetSet.add((byte) (-85));
    }

    public static boolean isOnStairs(Location location, int down) {
        return isUnderBlock(location, BlockUtil.blockStairsSet, down);
    }

    public static boolean isOnLiquid(Location location, int down) {
        return isUnderBlock(location, BlockUtil.blockLiquidsSet, down);
    }

    public static boolean isOnWeb(Location location, int down) {
        return isUnderBlock(location, BlockUtil.blockWebsSet, down);
    }

    public static boolean isOnIce(Location location, int down) {
        return isUnderBlock(location, BlockUtil.blockIceSet, down);
    }

    public static boolean isOnCarpet(Location location, int down) {
        return isUnderBlock(location, BlockUtil.blockCarpetSet, down);
    }

    public static boolean isLadder(Block block) {
        return block.getType().toString().contains("LADDER") || block.getType().toString().contains("VINE");
    }

    private static boolean isUnderBlock(Location location, Set<Byte> itemIDs, int down) {
        double posX=location.getX();
        double posZ=location.getZ();
        double fracX=(posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        double fracZ=(posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        int blockX=location.getBlockX();
        int blockY=location.getBlockY() - down;
        int blockZ=location.getBlockZ();
        World world=location.getWorld();
        if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracX > 0.7) {
            if (itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (itemIDs.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return itemIDs.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracZ < 0.3) {
            return itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId());
        } else return fracZ > 0.7 && itemIDs.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId());
        return false;
    }

    public static boolean isOnGround(Location location, int down) {
        double posX=location.getX();
        double posZ=location.getZ();
        double fracX=(posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        double fracZ=(posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        int blockX=location.getBlockX();
        int blockY=location.getBlockY() - down;
        int blockZ=location.getBlockZ();
        World world=location.getWorld();
        if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracX > 0.7) {
            if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId());
            } else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                return !BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId());
            }
        } else if (fracZ < 0.3) {
            return !BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId());
        } else return fracZ > 0.7 && !BlockUtil.blockSolidPassSet.contains((byte) world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId());
        return false;
    }

    public static boolean isOnFakeBlock(Player player, Location location, int down) {
        double posX=location.getX();
        double posZ=location.getZ();
        double fracX=(posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        double fracZ=(posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        int blockX=location.getBlockX();
        int blockY=location.getBlockY() - down;
        int blockZ=location.getBlockZ();
        PlayerData playerData= AntiCheat.instance.getPlayerDataManager().getPlayerData(player);
        Set<BlockPos> fakeBlocks=playerData.getFakeBlocks();
        if (fakeBlocks.contains(new BlockPos(blockX, blockY, blockZ))) {
            return true;
        }
        if (fracX < 0.3) {
            if (fakeBlocks.contains(new BlockPos(blockX - 1, blockY, blockZ))) {
                return true;
            }
            if (fracZ < 0.3) {
                if (fakeBlocks.contains(new BlockPos(blockX - 1, blockY, blockZ - 1))) {
                    return true;
                }
                if (fakeBlocks.contains(new BlockPos(blockX, blockY, blockZ - 1))) {
                    return true;
                }
                return fakeBlocks.contains(new BlockPos(blockX + 1, blockY, blockZ - 1));
            } else if (fracZ > 0.7) {
                if (fakeBlocks.contains(new BlockPos(blockX - 1, blockY, blockZ + 1))) {
                    return true;
                }
                if (fakeBlocks.contains(new BlockPos(blockX, blockY, blockZ + 1))) {
                    return true;
                }
                return fakeBlocks.contains(new BlockPos(blockX + 1, blockY, blockZ + 1));
            }
        } else if (fracX > 0.7) {
            if (fakeBlocks.contains(new BlockPos(blockX + 1, blockY, blockZ))) {
                return true;
            }
            if (fracZ < 0.3) {
                if (fakeBlocks.contains(new BlockPos(blockX - 1, blockY, blockZ - 1))) {
                    return true;
                }
                if (fakeBlocks.contains(new BlockPos(blockX, blockY, blockZ - 1))) {
                    return true;
                }
                return fakeBlocks.contains(new BlockPos(blockX + 1, blockY, blockZ - 1));
            } else if (fracZ > 0.7) {
                if (fakeBlocks.contains(new BlockPos(blockX - 1, blockY, blockZ + 1))) {
                    return true;
                }
                if (fakeBlocks.contains(new BlockPos(blockX, blockY, blockZ + 1))) {
                    return true;
                }
                return fakeBlocks.contains(new BlockPos(blockX + 1, blockY, blockZ + 1));
            }
        } else if (fracZ < 0.3) {
            return fakeBlocks.contains(new BlockPos(blockX, blockY, blockZ - 1));
        } else return fracZ > 0.7 && fakeBlocks.contains(new BlockPos(blockX, blockY, blockZ + 1));
        return false;
    }
}