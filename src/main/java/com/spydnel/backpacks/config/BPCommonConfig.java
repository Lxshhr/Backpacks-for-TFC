package com.spydnel.backpacks.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BPCommonConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue placeBackpackOnDeath;

    public static final ModConfigSpec.BooleanValue enableCuriosCompat;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("backpack");

        placeBackpackOnDeath = builder.comment("Place the backpack as a block on death").define("placeBackpackOnDeath", true);

        builder.pop();

        builder.push("compat");

        enableCuriosCompat = builder.comment("Enable equipping backpack in the curios slot").define("enableCuriosCompat", true);

        builder.pop();
        SPEC = builder.build();
    }
}
