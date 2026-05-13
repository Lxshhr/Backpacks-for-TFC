package com.spydnel.backpacks.utils;

import com.spydnel.backpacks.Backpacks;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public class BPUtils {
    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Backpacks.MOD_ID, path);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
