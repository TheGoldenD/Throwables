package com.auy.throwables;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.item.PokeBallItem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class CobblemonIntegration {

    private CobblemonIntegration() {
    }

    public static boolean isPokeBall(ItemStack stack) {
        return stack.getItem() instanceof PokeBallItem;
    }

    public static float getThrowPower(ItemStack stack) {
        PokeBallItem pokeBallItem =
                (PokeBallItem) stack.getItem();

        return pokeBallItem
                .getPokeBall()
                .getThrowPower();
    }

    public static ThrowableItemProjectile createProjectile(
            Level level,
            LivingEntity entity
    ) {
        InteractionHand hand =
                entity.getUsedItemHand();

        ItemStack stack =
                entity.getItemInHand(hand);

        if (!(stack.getItem() instanceof PokeBallItem pokeBallItem)) {
            return null;
        }

        return new EmptyPokeBallEntity(
                pokeBallItem.getPokeBall(),
                level,
                entity,
                CobblemonEntities.EMPTY_POKEBALL
        );
    }
}