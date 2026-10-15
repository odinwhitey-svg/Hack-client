package com.example.hackclient.module;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Detached camera. A throwaway RemotePlayer (never added to the level) becomes the camera entity,
 * and the real player's input is swapped for an empty one so your body stays put.
 */
public class Freecam extends Module {
    public double speed = 1.0;
    private RemotePlayer cam;
    private ClientInput savedInput;
    private float savedYaw, savedPitch;

    public Freecam() { super("Freecam"); }

    @Override protected void onEnable() {
        var p = mc.player;
        if (p == null || mc.level == null) return;
        savedYaw = p.getYRot();
        savedPitch = p.getXRot();
        cam = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), "Freecam"));
        cam.setPos(p.getX(), p.getY(), p.getZ());
        cam.setYRot(savedYaw); cam.setXRot(savedPitch);
        cam.yRotO = savedYaw; cam.xRotO = savedPitch;
        cam.xo = p.getX(); cam.yo = p.getY(); cam.zo = p.getZ();
        cam.noPhysics = true;
        mc.options.setCameraType(CameraType.FIRST_PERSON);
        savedInput = p.input;
        p.input = new ClientInput();
        mc.setCameraEntity(cam);
    }

    @Override protected void onDisable() {
        var p = mc.player;
        if (p != null) {
            if (savedInput != null) p.input = savedInput;
            p.setYRot(savedYaw); p.setXRot(savedPitch);
            mc.setCameraEntity(p);
        }
        cam = null; savedInput = null;
    }

    @Override public void onTick() {
        var p = mc.player;
        if (p == null || cam == null) return;
        if (p.input == savedInput) p.input = new ClientInput(); // something restored it

        cam.xo = cam.getX(); cam.yo = cam.getY(); cam.zo = cam.getZ();
        cam.yRotO = cam.getYRot(); cam.xRotO = cam.getXRot();
        cam.setYRot(p.getYRot());   // mouse look still turns the real player; the camera follows it
        cam.setXRot(p.getXRot());

        Options o = mc.options;
        Vec3 look = cam.getLookAngle();
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 d = Vec3.ZERO;
        if (o.keyUp.isDown())    d = d.add(look);
        if (o.keyDown.isDown())  d = d.subtract(look);
        if (o.keyRight.isDown()) d = d.add(right);
        if (o.keyLeft.isDown())  d = d.subtract(right);
        if (o.keyJump.isDown())  d = d.add(0, 1, 0);
        if (o.keyShift.isDown()) d = d.add(0, -1, 0);
        if (d.lengthSqr() > 0) {
            double s = speed * (o.keySprint.isDown() ? 2.5 : 1.0);
            d = d.normalize().scale(s);
            cam.setPos(cam.getX() + d.x, cam.getY() + d.y, cam.getZ() + d.z);
        }
    }
}
