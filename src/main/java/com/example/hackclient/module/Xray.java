package com.example.hackclient.module;

import com.example.hackclient.HackClient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class Xray extends Module {
    public static final Set<Block> BLOCKS = new HashSet<>(Set.of(
        Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
        Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
        Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
        Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
        Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS,
        Blocks.LAVA, Blocks.CHEST, Blocks.SPAWNER));

    public Xray() { super("Xray"); }

    public static boolean active() { return HackClient.MODULES != null && HackClient.MODULES.xray.isEnabled(); }

    /** Re-mesh chunks (also call after editing the block list). */
    public static void reload() {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.levelRenderer != null) mc.levelRenderer.allChanged();
    }

    @Override protected void onEnable() { reload(); }
    @Override protected void onDisable() { reload(); }
}
