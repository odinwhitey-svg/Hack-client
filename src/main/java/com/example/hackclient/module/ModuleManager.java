package com.example.hackclient.module;

import java.util.*;

public class ModuleManager {
    public final List<Module> all = new ArrayList<>();
    public final Freecam freecam = add(new Freecam());
    public final Fullbright fullbright = add(new Fullbright());
    public final Xray xray = add(new Xray());
    public final AutoBuild autoBuild = add(new AutoBuild());
    public final AutoBreak autoBreak = add(new AutoBreak());
    public final BaseFinder baseFinder = add(new BaseFinder());

    private <T extends Module> T add(T m) { all.add(m); return m; }

    public Module get(String name) {
        for (Module m : all) if (m.name.equalsIgnoreCase(name)) return m;
        return null;
    }

    public void tick() { for (Module m : all) if (m.isEnabled()) m.onTick(); }

    public void disableAll() { for (Module m : all) if (m.isEnabled()) m.setEnabled(false); }
}
