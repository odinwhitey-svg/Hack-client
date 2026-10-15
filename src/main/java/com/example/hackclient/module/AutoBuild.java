package com.example.hackclient.module;

import com.example.hackclient.Chat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Fills the selected region (bottom layer first) with the block in your main hand. */
public class AutoBuild extends Module {
    public int perTick = 2;
    public double reach = 4.5;

    public AutoBuild() { super("AutoBuild"); }

    @Override protected void onEnable() {
        if (!Region.ready() || Region.volume() > 200_000) {
            Chat.msg("Set .pos1 / .pos2 first (max 200k blocks).");
            // can't call setEnabled(false) inside onEnable's caller state change; schedule via tick
        }
    }

    @Override public void onTick() {
        var p = mc.player; var level = mc.level; var gm = mc.gameMode;
        if (p == null || level == null || gm == null || !Region.ready()) return;
        if (!(p.getMainHandItem().getItem() instanceof BlockItem)) return;

        Vec3 eye = p.getEyePosition();
        int placed = 0;
        for (int y = Region.minY(); y <= Region.maxY() && placed < perTick; y++) {
            for (int x = Region.minX(); x <= Region.maxX() && placed < perTick; x++) {
                for (int z = Region.minZ(); z <= Region.maxZ() && placed < perTick; z++) {
                    BlockPos t = new BlockPos(x, y, z);
                    if (!level.getBlockState(t).canBeReplaced()) continue;
                    if (Vec3.atCenterOf(t).distanceTo(eye) > reach) continue;
                    if (tryPlace(t)) placed++;
                }
            }
        }
    }

    private boolean tryPlace(BlockPos t) {
        var p = mc.player; var level = mc.level;
        for (Direction d : Direction.values()) {
            BlockPos n = t.relative(d);
            if (level.getBlockState(n).isAir() || level.getBlockState(n).canBeReplaced()) continue;
            Direction face = d.getOpposite();
            Vec3 hit = Vec3.atCenterOf(n).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
            mc.gameMode.useItemOn(p, InteractionHand.MAIN_HAND, new BlockHitResult(hit, face, n, false));
            p.swing(InteractionHand.MAIN_HAND);
            return true;
        }
        return false;
    }
}
