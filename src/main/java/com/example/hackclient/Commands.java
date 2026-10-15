package com.example.hackclient;

import com.example.hackclient.module.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Chat commands, prefix ".". Returns true if the message was consumed. */
public final class Commands {
    private Commands() {}

    public static boolean handle(String raw) {
        if (!raw.startsWith(".")) return false;
        String[] a = raw.substring(1).trim().split("\\s+");
        var M = HackClient.MODULES;
        var mc = Minecraft.getInstance();
        if (a.length == 0 || a[0].isEmpty()) return true;

        switch (a[0].toLowerCase()) {
            case "help" -> Chat.msg(".gui (or Right Shift) | .t <module> | .list | .pos1 | .pos2 | .xray add/remove <block> | .xray list | .speed <n> | .bf list|mode <base|amethyst|both>|min <n>");
            case "gui" -> HackClient.openGuiNext = true;
            case "list" -> { for (Module m : M.all) Chat.msg(m.name + ": " + (m.isEnabled() ? "ON" : "off")); }
            case "t", "toggle" -> {
                Module m = a.length > 1 ? M.get(a[1]) : null;
                if (m == null) Chat.msg("Unknown module. Try .list"); else m.toggle();
            }
            case "pos1", "pos2" -> {
                BlockPos p = lookedAt(mc);
                if (a[0].equals("pos1")) Region.pos1 = p; else Region.pos2 = p;
                Chat.msg(a[0] + " = " + p.getX() + " " + p.getY() + " " + p.getZ()
                    + (Region.ready() ? "  (" + Region.volume() + " blocks)" : ""));
            }
            case "speed" -> { if (a.length > 1) { M.freecam.speed = Double.parseDouble(a[1]); Chat.msg("Freecam speed " + M.freecam.speed); } }
            case "xray" -> xray(a);
            case "bf" -> bf(a, M.baseFinder);
            default -> Chat.msg("Unknown command. .help");
        }
        return true;
    }

    private static BlockPos lookedAt(Minecraft mc) {
        if (mc.hitResult instanceof BlockHitResult b && b.getType() == HitResult.Type.BLOCK) return b.getBlockPos();
        return mc.player.blockPosition();
    }

    private static void xray(String[] a) {
        if (a.length < 2) return;
        if (a[1].equals("list")) {
            StringBuilder sb = new StringBuilder();
            for (Block b : Xray.BLOCKS) sb.append(BuiltInRegistries.BLOCK.getKey(b).getPath()).append(' ');
            Chat.msg(sb.toString()); return;
        }
        if (a.length < 3) return;
        Identifier id = Identifier.tryParse(a[2].contains(":") ? a[2] : "minecraft:" + a[2]);
        Block b = id == null ? null : BuiltInRegistries.BLOCK.getValue(id);
        if (b == null) { Chat.msg("Unknown block"); return; }
        if (a[1].equals("add")) Xray.BLOCKS.add(b); else if (a[1].equals("remove")) Xray.BLOCKS.remove(b);
        Xray.reload();
        Chat.msg("Xray " + a[1] + " " + a[2]);
    }

    private static void bf(String[] a, BaseFinder f) {
        if (a.length < 2) return;
        switch (a[1]) {
            case "list" -> f.list();
            case "mode" -> { if (a.length > 2) { f.mode = BaseFinder.Mode.valueOf(a[2].toUpperCase()); Chat.msg("BaseFinder mode " + f.mode); } }
            case "min" -> { if (a.length > 2) { f.minScore = Integer.parseInt(a[2]); Chat.msg("BaseFinder min " + f.minScore); } }
        }
    }
}
