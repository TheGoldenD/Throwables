package com.auy.throwables;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.ModConfig;

@Mod(Throwables.MOD_ID)
public class Throwables {

    public static final String MOD_ID = "throwables";

    public Throwables(ModContainer container) {
        container.registerConfig(
                ModConfig.Type.SERVER,
                ThrowableConfig.CONFIG_SPEC
        );
    }
}