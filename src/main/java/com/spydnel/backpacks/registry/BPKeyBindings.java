package com.spydnel.backpacks.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class BPKeyBindings {
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("backpacks.key.open_backpack", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "Backpacks");
}
