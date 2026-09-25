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
        modid = Throwables.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class ThrowableEvents {

    public static final int MAX_USE_TICKS = 72000;

    private ThrowableEvents() {
    }

    /**
     * Intercepts right-clicking an egg or snowball.
     *
     * Normally Minecraft immediately throws these items.
     * We cancel that behavior and put the player into the
     * normal item-use state instead.
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
     * Changes the use duration of eggs and snowballs
     * so the player can hold right-click.
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
     * The charge time determines the projectile velocity.
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
         * Only the server should create the projectile.
         * Otherwise both client and server could create one.
         */
        if (entity.level().isClientSide) {
            return;
        }

        ThrowableItemProjectile projectile =
                definition.projectileFactory()
                        .apply(entity.level(), entity);

        /*
         * Keep one copy of the original item on the
         * projectile.
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
                            event.getHand()
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
     * Returns the throwing configuration for supported
     * vanilla items.
     */
    private static baller definitionFor(
            ItemStack stack
    ) {
        /*
         * Vanilla Egg
         */
        if (stack.is(Items.EGG)) {
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
```
