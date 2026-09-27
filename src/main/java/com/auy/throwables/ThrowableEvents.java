package com.auy.throwables;

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
import net.neoforged.fml.ModList;
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

        baller definition =
                definitionFor(event.getItem());

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

        if (projectile == null) {
            return;
        }

        projectile.setItem(
                event.getItem().copyWithCount(1)
        );

        float velocity =
                definition.velocityForCharge(chargeTicks);

        /*
         * Cobblemon's normal Poké Ball throw uses a small
         * overhand adjustment. Keep that behavior while
         * replacing its fixed throw power with our charged
         * velocity.
         */
        if (
                ModList.get().isLoaded("cobblemon")
                        && CobblemonIntegration.isPokeBall(event.getItem())
        ) {
            float overhandFactor =
                    entity.getXRot() < 0.0F
                            ? 5.0F * (float) Math.cos(
                            Math.toRadians(entity.getXRot())
                    )
                            : 5.0F;

            projectile.shootFromRotation(
                    entity,
                    entity.getXRot() - overhandFactor,
                    entity.getYRot(),
                    0.0F,
                    velocity,
                    definition.inaccuracy()
            );

            projectile.setPos(
                    projectile.position()
                            .add(
                                    projectile
                                            .getDeltaMovement()
                                            .normalize()
                                            .scale(1.0)
                            )
            );
        } else {
            projectile.shootFromRotation(
                    entity,
                    entity.getXRot(),
                    entity.getYRot(),
                    0.0F,
                    velocity,
                    definition.inaccuracy()
            );
        }

        entity.level().addFreshEntity(projectile);

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

    public static baller definitionFor(ItemStack stack) {

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

        /*
         * Optional Cobblemon integration.
         *
         * PokeBallItem is only referenced after checking that
         * Cobblemon is actually loaded, keeping Throwables
         * usable without Cobblemon installed.
         */
        if (
                ThrowableConfig.ENABLE_COBBLEMON_POKEBALLS.get()
                        && ModList.get().isLoaded("cobblemon")
                        && CobblemonIntegration.isPokeBall(stack)
        ) {
            float throwPower =
                    CobblemonIntegration.getThrowPower(stack);

            float minVelocity =
                    Math.max(
                            0.35F,
                            throwPower * 0.5F
                    );

            return baller.of(
                    CobblemonIntegration::createProjectile,
                    minVelocity,
                    throwPower
            );
        }

        return null;
    }
}