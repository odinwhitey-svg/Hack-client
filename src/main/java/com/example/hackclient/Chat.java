package com.example.hackclient;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class Chat {
    private Chat() {}
    public static void msg(String s) {
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal("[HC] " + s));
    }
}
