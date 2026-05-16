package com.spydnel.backpacks.registry;

import com.spydnel.backpacks.utils.BPUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class BPLayers {
    public static final ModelLayerLocation BACKPACK = getLocation("backpack");
    public static final ModelLayerLocation BACKPACK_BLOCK = getLocation("backpack_block");

    private static ModelLayerLocation getLocation(String name) {
        return new ModelLayerLocation(BPUtils.loc(name), "main");
    }
}
