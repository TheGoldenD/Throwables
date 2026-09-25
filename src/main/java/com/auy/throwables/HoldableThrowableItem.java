package com.auy.throwables;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class HoldableThrowableItem extends Item {

    private final baller definition;

    public HoldableThrowableItem(
            Properties properties,
            baller definition
    ) {
        super(properties);
        this.definition = definition;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        player.startUsingItem(hand);

        return InteractionResultHolder.consume(
                player.getItemInHand(hand)
        );
    }

    @Override
    public int getUseDuration(
            ItemStack stack,
            LivingEntity entity
    ) {
        return ThrowableEvents.MAX_USE_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public void releaseUsing(
            ItemStack stack,
            Level level,
            LivingEntity entity,
            int timeLeft
    ) {
        int chargeTicks =
                getUseDuration(stack, entity) - timeLeft;

        if (chargeTicks < definition.minChargeTicks()) {
            return;
        }

        if (!level.isClientSide) {
            float velocity =
                    definition.velocityForCharge(chargeTicks);

            ThrowableItemProjectile projectile =
                    definition.projectileFactory()
                            .apply(level, entity);

            projectile.setItem(
                    stack.copyWithCount(1)
            );

            projectile.shootFromRotation(
                    entity,
                    entity.getXRot(),
                    entity.getYRot(),
                    0.0F,
                    velocity,
                    definition.inaccuracy()
            );

            level.addFreshEntity(projectile);
        }

        if (
                entity instanceof Player player
                        && !player.getAbilities().instabuild
        ) {
            stack.shrink(1);
        }
    }
}