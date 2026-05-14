package com.spydnel.backpacks.config;

import net.dries007.tfc.common.component.size.Size;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.EnumValue<Size> backpackMaximumItemSize;
    public static final ModConfigSpec.BooleanValue placeBackpackOnDeath;

    public static final ModConfigSpec.BooleanValue enableCuriosCompat;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("backpack");
        backpackMaximumItemSize = builder.comment("The largest (inclusive) size of an item that is allowed in a backpack").defineEnum("backpackMaximumItemSize", Size.LARGE);
        placeBackpackOnDeath = builder.comment("Place the backpack as a block on death").define("placeBackpackOnDeath", true);

        builder.pop();

        builder.push("compat");
        enableCuriosCompat = builder.comment("Enable equipping backpack in the curios slot").define("enableCuriosCompat", true);

        builder.pop();
        SPEC = builder.build();
    }
}
