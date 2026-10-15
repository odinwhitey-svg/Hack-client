package com.example.hackclient.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

/** Mines the selected region top-down, nearest block first, using whatever tool is held. */
public class AutoBreak extends Module {
    public double reach = 4.5;
    private BlockPos target;

    public AutoBreak() { super("AutoBreak"); }

    @Override protected void onDisable() {
        target = null;
        if (mc.gameMode != null) mc.gameMode.stopDestroyBlock();
    }

    @Override public void onTick() {
        var p = mc.player; var level = mc.level; var gm = mc.gameMode;
        if (p == null || level == null || gm == null || !Region.ready()) return;

        if (target == null || level.getBlockState(target).isAir()) target = pick();
        if (target == null) return;

        Vec3 eye = p.getEyePosition();
        Vec3 c = Vec3.atCenterOf(target);
        Direction face = Direction.getApproximateNearest(eye.x - c.x, eye.y - c.y, eye.z - c.z);
        gm.continueDestroyBlock(target, face);
        p.swing(InteractionHand.MAIN_HAND);
    }

    private BlockPos pick() {
        var level = mc.level; Vec3 eye = mc.player.getEyePosition();
        BlockPos best = null; double bestD = Double.MAX_VALUE;
        for (int y = Region.maxY(); y >= Region.minY(); y--) {
            for (int x = Region.minX(); x <= Region.maxX(); x++) {
                for (int z = Region.minZ(); z <= Region.maxZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    var s = level.getBlockState(pos);
                    if (s.isAir() || s.getDestroySpeed(level, pos) < 0) continue;
                    double d = Vec3.atCenterOf(pos).distanceTo(eye);
                    if (d <= reach && d < bestD) { bestD = d; best = pos; }
                }
            }
            if (best != null) return best; // finish a layer before going lower
        }
        return null;
    }
}
