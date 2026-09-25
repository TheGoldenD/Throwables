package com.auy.throwables;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ThrowableConfig {

    public static final ModConfigSpec CONFIG_SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_EGG;
    public static final ModConfigSpec.BooleanValue ENABLE_SNOWBALL;

    static {
        ModConfigSpec.Builder builder =
                new ModConfigSpec.Builder();

        builder.push("throwables");

        ENABLE_EGG = builder
                .comment("Enable charged throwing for eggs.")
                .define("enable_egg", true);

        ENABLE_SNOWBALL = builder
                .comment("Enable charged throwing for snowballs.")
                .define("enable_snowball", true);

        builder.pop();

        CONFIG_SPEC = builder.build();
    }

    private ThrowableConfig() {
    }
}