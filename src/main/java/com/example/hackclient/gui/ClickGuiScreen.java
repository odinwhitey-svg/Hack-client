package com.example.hackclient.gui;

import com.example.hackclient.Commands;
import com.example.hackclient.HackClient;
import com.example.hackclient.module.BaseFinder;
import com.example.hackclient.module.Module;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Simple menu built entirely from vanilla widgets (no custom draw code, so it survives renderer changes). */
public class ClickGuiScreen extends Screen {
    private static final int W = 124, H = 20, GAP = 4, COLS = 2;

    public ClickGuiScreen() { super(Component.literal("HackClient")); }

    @Override protected void init() {
        List<Module> mods = HackClient.MODULES.all;
        int rows = (mods.size() + COLS - 1) / COLS;
        int totalW = COLS * W + (COLS - 1) * GAP;
        int x0 = (width - totalW) / 2;
        int totalH = 18 + rows * (H + GAP) + 3 * (H + GAP) + H;
        int y = (height - totalH) / 2;

        addRenderableWidget(new StringWidget(x0, y, totalW, 14,
            Component.literal("HackClient").withStyle(ChatFormatting.BOLD), font).alignCenter());
        y += 18;

        for (int i = 0; i < mods.size(); i++) {
            Module m = mods.get(i);
            int x = x0 + (i % COLS) * (W + GAP);
            int yy = y + (i / COLS) * (H + GAP);
            addRenderableWidget(Button.builder(label(m), b -> { m.toggle(); b.setMessage(label(m)); })
                .bounds(x, yy, W, H).build());
        }
        y += rows * (H + GAP);

        addRenderableWidget(new SpeedSlider(x0, y, totalW, H));
        y += H + GAP;

        addRenderableWidget(Button.builder(Component.literal("Set Pos 1"), b -> Commands.handle(".pos1"))
            .bounds(x0, y, W, H).build());
        addRenderableWidget(Button.builder(Component.literal("Set Pos 2"), b -> Commands.handle(".pos2"))
            .bounds(x0 + W + GAP, y, W, H).build());
        y += H + GAP;

        BaseFinder bf = HackClient.MODULES.baseFinder;
        addRenderableWidget(Button.builder(Component.literal("Finder: " + bf.mode), b -> {
            BaseFinder.Mode[] v = BaseFinder.Mode.values();
            bf.mode = v[(bf.mode.ordinal() + 1) % v.length];
            b.setMessage(Component.literal("Finder: " + bf.mode));
        }).bounds(x0, y, W, H).build());
        addRenderableWidget(Button.builder(Component.literal("Show finds"), b -> { bf.list(); onClose(); })
            .bounds(x0 + W + GAP, y, W, H).build());
        y += H + GAP;

        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
            .bounds(x0, y, totalW, H).build());
    }

    private static Component label(Module m) {
        boolean on = m.isEnabled();
        return Component.literal(m.name + "  ")
            .append(Component.literal(on ? "ON" : "OFF").withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    @Override public boolean isPauseScreen() { return false; }

    private static final class SpeedSlider extends AbstractSliderButton {
        SpeedSlider(int x, int y, int w, int h) {
            super(x, y, w, h, Component.empty(), (HackClient.MODULES.freecam.speed - 0.2) / 4.8);
            updateMessage();
        }
        @Override protected void updateMessage() {
            setMessage(Component.literal(String.format("Freecam speed: %.1f", 0.2 + value * 4.8)));
        }
        @Override protected void applyValue() { HackClient.MODULES.freecam.speed = 0.2 + value * 4.8; }
    }
}
