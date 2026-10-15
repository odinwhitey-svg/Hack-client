package com.example.hackclient.module;

import com.example.hackclient.Chat;
import net.minecraft.client.Minecraft;

public abstract class Module {
    public final String name;
    private boolean enabled;
    protected final Minecraft mc = Minecraft.getInstance();

    protected Module(String name) { this.name = name; }

    public boolean isEnabled() { return enabled; }
    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean e) {
        if (e == enabled) return;
        enabled = e;
        if (e) onEnable(); else onDisable();
        Chat.msg(name + (e ? " ON" : " OFF"));
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
}
