package com.auy.throwables;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Throwables.MOD_ID)
public final class ThrowableEvents {

    public static final int MAX_USE_TICKS = 72000;

    private ThrowableEvents() {
    }

    @SubscribeEvent
    public static void onRightClickItem(
            PlayerInteractEvent.RightClickItem event
    ) {
        ItemStack stack = event.getItemStack();

        if (definitionFor(stack) == null) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.CONSUME);

        event.getEntity().startUsingItem(event.getHand());
    }

    @SubscribeEvent
    public static void onUseStart(
            LivingEntityUseItemEvent.Start event
    ) {
        if (definitionFor(event.getItem()) == null) {
            return;
        }

        event.setDuration(MAX_USE_TICKS);
    }

    @SubscribeEvent
    public static void onUseStop(
            LivingEntityUseItemEvent.Stop event
    ) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();

        baller definition = definitionFor(stack);

        if (definition == null) {
            return;
        }

        int chargeTicks =
                MAX_USE_TICKS - event.getDuration();

        if (chargeTicks < definition.minChargeTicks()) {
            return;
        }

        if (entity.level().isClientSide) {
            return;
        }

        ThrowableItemProjectile projectile =
                definition.projectileFactory()
                        .apply(entity.level(), entity);

        projectile.setItem(
                stack.copyWithCount(1)
        );

        float velocity =
                definition.velocityForCharge(chargeTicks);

        projectile.shootFromRotation(
                entity,
                entity.getXRot(),
                entity.getYRot(),
                0.0F,
                velocity,
                definition.inaccuracy()
        );

        entity.level().addFreshEntity(projectile);

        playThrowSound(
                entity,
                stack
        );

        if (
                entity instanceof Player player
                        && !player.getAbilities().instabuild
        ) {
            ItemStack heldStack =
                    player.getItemInHand(
                            player.getUsedItemHand()
                    );

            if (
                    heldStack.is(
                            stack.getItem()
                    )
            ) {
                heldStack.shrink(1);
            }
        }
    }

    private static void playThrowSound(
            LivingEntity entity,
            ItemStack stack
    ) {
        SoundEvent sound = soundFor(stack);

        if (sound == null) {
            return;
        }

        entity.level().playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                sound,
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (
                        entity.level().getRandom().nextFloat()
                                * 0.4F
                                + 0.8F
                )
        );
    }

    private static SoundEvent soundFor(
            ItemStack stack
    ) {
        if (stack.is(Items.EGG)) {
            return SoundEvents.EGG_THROW;
        }

        if (stack.is(Items.SNOWBALL)) {
            return SoundEvents.SNOWBALL_THROW;
        }

        if (stack.is(Items.ENDER_PEARL)) {
            return SoundEvents.ENDER_PEARL_THROW;
        }

        if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            return SoundEvents.EXPERIENCE_BOTTLE_THROW;
        }

        if (stack.is(Items.SPLASH_POTION)) {
            return SoundEvents.SPLASH_POTION_THROW;
        }

        if (stack.is(Items.LINGERING_POTION)) {
            return SoundEvents.LINGERING_POTION_THROW;
        }

        return null;
    }

    private static baller definitionFor(
            ItemStack stack
    ) {
        if (
                stack.is(Items.EGG)
                        && ThrowableConfig.ENABLE_EGG.get()
        ) {
            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new ThrownEgg(
                                    level,
                                    entity
                            )
            );
        }

        if (
                stack.is(Items.SNOWBALL)
                        && ThrowableConfig.ENABLE_SNOWBALL.get()
        ) {
            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new Snowball(
                                    level,
                                    entity
                            )
            );
        }

        if (
                stack.is(Items.ENDER_PEARL)
                        && ThrowableConfig.ENABLE_ENDER_PEARL.get()
        ) {
            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new ThrownEnderpearl(
                                    level,
                                    entity
                            )
            );
        }

        if (
                stack.is(Items.EXPERIENCE_BOTTLE)
                        && ThrowableConfig.ENABLE_EXPERIENCE_BOTTLE.get()
        ) {
            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new ThrownExperienceBottle(
                                    level,
                                    entity
                            )
            );
        }

        if (
                stack.is(Items.SPLASH_POTION)
                        && ThrowableConfig.ENABLE_SPLASH_POTION.get()
        ) {
            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new ThrownPotion(
                                    level,
                                    entity
                            )
            );
        }

        if (
                stack.is(Items.LINGERING_POTION)
                        && ThrowableConfig.ENABLE_LINGERING_POTION.get()
        ) {
            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new ThrownPotion(
                                    level,
                                    entity
                            )
            );
        }

        return null;
    }
}