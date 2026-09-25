package com.auy.throwables;

import java.util.function.BiFunction;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
public record baller(
        int minChargeTicks,
        int maxChargeTicks,
        float minVelocity,
        float maxVelocity,
        float inaccuracy,
        BiFunction<Level, LivingEntity, ThrowableItemProjectile> projectileFactory
) {
    public static baller of(BiFunction<Level, LivingEntity, ThrowableItemProjectile> factory) {
        return new baller(5, 20, 0.8F, 1.8F, 1.0F, factory);
    }
    public float velocityForCharge(int chargeTicks) {
        int clampedTicks = Mth.clamp(chargeTicks, minChargeTicks, maxChargeTicks);
        float progress = (maxChargeTicks == minChargeTicks)
                ? 1.0F
                : (float) (clampedTicks - minChargeTicks) / (maxChargeTicks - minChargeTicks);
        return Mth.lerp(progress, minVelocity, maxVelocity);
    }
}