package com.example.hackclient.mixin;

import com.example.hackclient.module.Xray;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Xray: only draw faces of whitelisted blocks.
 * The full descriptor is spelled out so that if this signature doesn't exist in your exact
 * version, the injection is skipped (require = 0) instead of crashing. Check Block.shouldRenderFace
 * in your decompiled 26.2 jar and adjust the descriptor + parameters if Xray does nothing.
 * Vanilla chunk renderer only: Sodium/Iris use their own face culling.
 */
@Mixin(Block.class)
public class XrayMixin {
    @Inject(
        method = "shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
        at = @At("HEAD"), cancellable = true, require = 0)
    private static void hackclient$xray(BlockState state, BlockState neighbor, Direction dir,
                                        CallbackInfoReturnable<Boolean> cir) {
        if (Xray.active()) cir.setReturnValue(Xray.BLOCKS.contains(state.getBlock()));
    }
}
