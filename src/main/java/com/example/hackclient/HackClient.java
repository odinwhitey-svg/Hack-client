package com.example.hackclient;

import com.example.hackclient.gui.ClickGuiScreen;
import com.example.hackclient.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class HackClient implements ClientModInitializer {
    public static ModuleManager MODULES;
    /** Set by ".gui"; handled next tick because the chat screen closes right after sending. */
    public static boolean openGuiNext;

    @Override public void onInitializeClient() {
        MODULES = new ModuleManager();
        Keys.register();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || mc.level == null) { MODULES.disableAll(); return; }
            while (Keys.OPEN.consumeClick()) openGuiNext = true;
            if (openGuiNext && mc.screen == null) { openGuiNext = false; mc.setScreen(new ClickGuiScreen()); }
            MODULES.tick();
        });

        ClientSendMessageEvents.ALLOW_CHAT.register(msg -> !Commands.handle(msg));
    }
}
