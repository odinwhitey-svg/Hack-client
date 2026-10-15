package com.example.hackclient.module;

import com.example.hackclient.Chat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.*;

/**
 * Scans chunks you have loaded (render distance) for player-built indicators:
 *  - BASE: storage / utility block entities (chests, barrels, shulkers, furnaces, hoppers, beds...)
 *  - AMETHYST: amethyst blocks/buds/clusters (also matches natural geodes, so it's reported separately)
 * Results are chat/command output only; no world-space rendering.
 */
public class BaseFinder extends Module {
    public enum Mode { BASE, AMETHYST, BOTH }
    public Mode mode = Mode.BOTH;
    public int minScore = 3;
    public int chunksPerTick = 6;

    private static final Set<Block> BASE_BLOCKS = Set.of(
        Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL, Blocks.ENDER_CHEST, Blocks.HOPPER,
        Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.DROPPER, Blocks.DISPENSER,
        Blocks.BEACON, Blocks.BREWING_STAND, Blocks.ENCHANTING_TABLE, Blocks.LECTERN);
    private static final Set<Block> AMETHYST = Set.of(
        Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST, Blocks.SMALL_AMETHYST_BUD,
        Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER);

    public static final class Hit {
        public final ChunkPos pos; public int base, amethyst; public BlockPos sample;
        Hit(ChunkPos p) { pos = p; }
        int score(Mode m) { return (m != Mode.AMETHYST ? base : 0) + (m != Mode.BASE ? amethyst : 0); }
    }

    private final Map<ChunkPos, Hit> hits = new HashMap<>();
    private final Set<ChunkPos> announced = new HashSet<>();
    private List<ChunkPos> queue = new ArrayList<>();
    private int idx;

    public BaseFinder() { super("BaseFinder"); }

    @Override protected void onEnable() { hits.clear(); announced.clear(); queue.clear(); idx = 0; }

    @Override public void onTick() {
        var p = mc.player; var level = mc.level;
        if (p == null || level == null) return;
        if (idx >= queue.size()) rebuildQueue();
        for (int i = 0; i < chunksPerTick && idx < queue.size(); i++) scan(queue.get(idx++));
    }

    private void rebuildQueue() {
        queue = new ArrayList<>(); idx = 0;
        ChunkPos c = mc.player.chunkPosition();
        int r = mc.options.getEffectiveRenderDistance();
        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++)
            queue.add(new ChunkPos(c.x() + dx, c.z() + dz));
    }

    private void scan(ChunkPos cp) {
        var chunkAccess = mc.level.getChunkSource().getChunk(cp.x(), cp.z(), ChunkStatus.FULL, false);
        if (!(chunkAccess instanceof LevelChunk ch)) return;
        Hit h = new Hit(cp);

        if (mode != Mode.AMETHYST) {
            for (var be : ch.getBlockEntities().values()) {
                if (BASE_BLOCKS.contains(be.getBlockState().getBlock()) || be.getBlockState().getBlock().getDescriptionId().contains("shulker_box")) {
                    h.base++; if (h.sample == null) h.sample = be.getBlockPos();
                }
            }
        }
        if (mode != Mode.BASE) {
            LevelChunkSection[] secs = ch.getSections();
            for (int i = 0; i < secs.length; i++) {
                LevelChunkSection s = secs[i];
                if (s == null || s.hasOnlyAir() || !s.maybeHas(st -> AMETHYST.contains(st.getBlock()))) continue;
                int y0 = (ch.getMinSectionY() + i) << 4;
                for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) {
                    if (AMETHYST.contains(s.getBlockState(x, y, z).getBlock())) {
                        h.amethyst++;
                        if (h.sample == null) h.sample = new BlockPos(cp.getMinBlockX() + x, y0 + y, cp.getMinBlockZ() + z);
                    }
                }
            }
        }
        if (h.score(mode) == 0) { hits.remove(cp); return; }
        hits.put(cp, h);
        if (h.score(mode) >= minScore && announced.add(cp)) {
            Chat.msg("Found: " + describe(h));
        }
    }

    private static String describe(Hit h) {
        return "chunk " + h.pos.x() + "," + h.pos.z() + " @ " + h.sample.getX() + " " + h.sample.getY() + " " + h.sample.getZ()
            + "  (base:" + h.base + " amethyst:" + h.amethyst + ")";
    }

    public void list() {
        if (hits.isEmpty()) { Chat.msg("Nothing found yet."); return; }
        var p = mc.player.blockPosition();
        hits.values().stream().filter(h -> h.score(mode) >= minScore)
            .sorted(Comparator.comparingDouble(h -> h.sample.distSqr(p)))
            .limit(10).forEach(h -> Chat.msg(describe(h)));
    }
}
