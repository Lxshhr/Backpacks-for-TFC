package com.spydnel.backpacks.config;

import net.dries007.tfc.common.component.size.Size;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.EnumValue<Size> backpackMaximumItemSize;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("backpack");
        backpackMaximumItemSize = builder.comment("The largest (inclusive) size of an item that is allowed in a backpack").defineEnum("backpackMaximumItemSize", Size.LARGE);

        builder.pop();
        SPEC = builder.build();
    }
}
