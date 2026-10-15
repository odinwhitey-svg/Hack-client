package com.example.hackclient.module;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Client-side night vision. */
public class Fullbright extends Module {
    public Fullbright() { super("Fullbright"); }

    @Override public void onTick() {
        var p = mc.player;
        if (p != null && !p.hasEffect(MobEffects.NIGHT_VISION)) {
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,
                MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
        }
    }

    @Override protected void onDisable() {
        if (mc.player != null) mc.player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
