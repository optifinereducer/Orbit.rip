package rip.warzone.anticheat.util;

import java.io.Serializable;

public class BlockPos implements Comparable<BlockPos>, Serializable {

    private static final long serialVersionUID=-8966009362001100977L;
    private final int x;
    private final int y;
    private final int z;
    public static BlockPos ORIGIN;

    static {
        ORIGIN=new BlockPos(0, 0, 0);
    }

    public BlockPos(int x, int y, int z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BlockPos)) {
            return false;
        }
        BlockPos p=(BlockPos) other;
        return this.x == p.x && this.y == p.y && this.z == p.z;
    }

    @Override
    public int hashCode() {
        return (this.y & 0xFF) | (this.x & 0x7FFF) << 8 | (this.z & 0x7FFF) << 24 | ((this.x < 0) ? Integer.MIN_VALUE : 0) | ((this.z < 0) ? 32768 : 0);
    }

    @Override
    public int compareTo(BlockPos pos) {
        if (this.y != pos.y) {
            return this.y - pos.y;
        }
        if (this.z == pos.z) {
            return this.x - pos.x;
        }
        return this.z - pos.z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public BlockPos offset(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    public BlockPos above() {
        return new BlockPos(this.x, this.y + 1, this.z);
    }

    public BlockPos above(int steps) {
        return new BlockPos(this.x, this.y + steps, this.z);
    }

    public BlockPos below() {
        return new BlockPos(this.x, this.y - 1, this.z);
    }

    public BlockPos below(int steps) {
        return new BlockPos(this.x, this.y - steps, this.z);
    }

    public BlockPos north() {
        return new BlockPos(this.x, this.y, this.z - 1);
    }

    public BlockPos north(int steps) {
        return new BlockPos(this.x, this.y, this.z - steps);
    }

    public BlockPos south() {
        return new BlockPos(this.x, this.y, this.z + 1);
    }

    public BlockPos south(int steps) {
        return new BlockPos(this.x, this.y, this.z + steps);
    }

    public BlockPos west() {
        return new BlockPos(this.x - 1, this.y, this.z);
    }

    public BlockPos west(int steps) {
        return new BlockPos(this.x - steps, this.y, this.z);
    }

    public BlockPos east() {
        return new BlockPos(this.x + 1, this.y, this.z);
    }

    public BlockPos east(int steps) {
        return new BlockPos(this.x + steps, this.y, this.z);
    }

    public double dist(int x, int y, int z) {
        int dx=this.x - x;
        int dy=this.y - y;
        int dz=this.z - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double dist(BlockPos pos) {
        return this.dist(pos.x, pos.y, pos.z);
    }

    public double distSqr(int x, int y, int z) {
        int dx=this.x - x;
        int dy=this.y - y;
        int dz=this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distSqr(float x, float y, float z) {
        float dx=this.x - x;
        float dy=this.y - y;
        float dz=this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distSqr(double x, double y, double z) {
        double dx=this.x - x;
        double dy=this.y - y;
        double dz=this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distSqr(BlockPos pos) {
        return this.distSqr(pos.x, pos.y, pos.z);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
