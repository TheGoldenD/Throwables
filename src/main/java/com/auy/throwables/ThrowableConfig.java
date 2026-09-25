package com.auy.throwables;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ThrowableConfig {

    public static final ModConfigSpec CONFIG_SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_EGG;
    public static final ModConfigSpec.BooleanValue ENABLE_SNOWBALL;
    public static final ModConfigSpec.BooleanValue ENABLE_ENDER_PEARL;
    public static final ModConfigSpec.BooleanValue ENABLE_EXPERIENCE_BOTTLE;
    public static final ModConfigSpec.BooleanValue ENABLE_SPLASH_POTION;
    public static final ModConfigSpec.BooleanValue ENABLE_LINGERING_POTION;

    static {
        ModConfigSpec.Builder builder =
                new ModConfigSpec.Builder();

        builder.push("throwables");

        ENABLE_EGG = builder
                .comment("Enable charged throwing for eggs.")
                .translation("config.throwables.enable_egg")
                .define("enable_egg", true);

        ENABLE_SNOWBALL = builder
                .comment("Enable charged throwing for snowballs.")
                .translation("config.throwables.enable_snowball")
                .define("enable_snowball", true);

        ENABLE_ENDER_PEARL = builder
                .comment("Enable charged throwing for ender pearls.")
                .translation("config.throwables.enable_ender_pearl")
                .define("enable_ender_pearl", true);

        ENABLE_EXPERIENCE_BOTTLE = builder
                .comment("Enable charged throwing for experience bottles.")
                .translation("config.throwables.enable_experience_bottle")
                .define("enable_experience_bottle", true);

        ENABLE_SPLASH_POTION = builder
                .comment("Enable charged throwing for splash potions.")
                .translation("config.throwables.enable_splash_potion")
                .define("enable_splash_potion", true);

        ENABLE_LINGERING_POTION = builder
                .comment("Enable charged throwing for lingering potions.")
                .translation("config.throwables.enable_lingering_potion")
                .define("enable_lingering_potion", true);

        builder.pop();

        CONFIG_SPEC = builder.build();
    }

    private ThrowableConfig() {
    }
}