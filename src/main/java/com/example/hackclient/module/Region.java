package com.example.hackclient.module;

import net.minecraft.core.BlockPos;

/** Shared selection used by AutoBuild / AutoBreak. */
public final class Region {
    public static BlockPos pos1, pos2;
    private Region() {}

    public static boolean ready() { return pos1 != null && pos2 != null; }
    public static int minX() { return Math.min(pos1.getX(), pos2.getX()); }
    public static int maxX() { return Math.max(pos1.getX(), pos2.getX()); }
    public static int minY() { return Math.min(pos1.getY(), pos2.getY()); }
    public static int maxY() { return Math.max(pos1.getY(), pos2.getY()); }
    public static int minZ() { return Math.min(pos1.getZ(), pos2.getZ()); }
    public static int maxZ() { return Math.max(pos1.getZ(), pos2.getZ()); }
    public static long volume() {
        return (long) (maxX() - minX() + 1) * (maxY() - minY() + 1) * (maxZ() - minZ() + 1);
    }
}
