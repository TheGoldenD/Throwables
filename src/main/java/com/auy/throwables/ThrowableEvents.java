package com.auy.throwables;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(
        modid = Throwables.MOD_ID
)
public final class ThrowableEvents {

    public static final int MAX_USE_TICKS = 72000;

    private ThrowableEvents() {
    }

    /**
     * Intercepts right-clicking a supported throwable.
     *
     * If the throwable is disabled in the config,
     * vanilla Minecraft behavior is left untouched.
     */
    @SubscribeEvent
    public static void onRightClickItem(
            PlayerInteractEvent.RightClickItem event
    ) {
        ItemStack stack = event.getItemStack();

        if (definitionFor(stack) == null) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(
                InteractionResult.CONSUME
        );

        event.getEntity().startUsingItem(
                event.getHand()
        );
    }

    /**
     * Changes the use duration so the player can
     * hold right-click.
     */
    @SubscribeEvent
    public static void onUseStart(
            LivingEntityUseItemEvent.Start event
    ) {
        if (definitionFor(event.getItem()) == null) {
            return;
        }

        event.setDuration(MAX_USE_TICKS);
    }

    /**
     * Called when the player releases right-click.
     *
     * Charge time determines projectile velocity.
     */
    @SubscribeEvent
    public static void onUseStop(
            LivingEntityUseItemEvent.Stop event
    ) {
        LivingEntity entity = event.getEntity();

        baller definition =
                definitionFor(event.getItem());

        if (definition == null) {
            return;
        }

        int chargeTicks =
                MAX_USE_TICKS - event.getDuration();

        if (
                chargeTicks
                        < definition.minChargeTicks()
        ) {
            return;
        }

        /*
         * Only the server creates the projectile.
         */
        if (entity.level().isClientSide) {
            return;
        }

        ThrowableItemProjectile projectile =
                definition.projectileFactory()
                        .apply(entity.level(), entity);

        /*
         * Keep one copy of the original item
         * on the projectile.
         */
        projectile.setItem(
                event.getItem().copyWithCount(1)
        );

        float velocity =
                definition.velocityForCharge(
                        chargeTicks
                );

        projectile.shootFromRotation(
                entity,
                entity.getXRot(),
                entity.getYRot(),
                0.0F,
                velocity,
                definition.inaccuracy()
        );

        entity.level().addFreshEntity(projectile);

        /*
         * Consume one item in Survival.
         */
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
                            event.getItem().getItem()
                    )
            ) {
                heldStack.shrink(1);
            }
        }
    }

    /**
     * Returns the throwing configuration for a supported
     * vanilla item.
     *
     * Returning null means the item should behave normally.
     */
    private static baller definitionFor(
            ItemStack stack
    ) {
        /*
         * Vanilla Egg
         */
        if (stack.is(Items.EGG)) {

            if (!ThrowableConfig.ENABLE_EGG.get()) {
                return null;
            }

            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new ThrownEgg(
                                    level,
                                    entity
                            )
            );
        }

        /*
         * Vanilla Snowball
         */
        if (stack.is(Items.SNOWBALL)) {

            if (!ThrowableConfig.ENABLE_SNOWBALL.get()) {
                return null;
            }

            return baller.of(
                    (Level level, LivingEntity entity) ->
                            new Snowball(
                                    level,
                                    entity
                            )
            );
        }

        return null;
    }
}