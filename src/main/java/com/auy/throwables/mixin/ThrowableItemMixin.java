package com.auy.throwables.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.auy.throwables.ThrowableEvents;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

/**
 * Makes the REAL vanilla item (Snowball, Egg, etc.) report itself as a
 * spear-style charge-and-throw item while our system considers it
 * "holdable and throwable" (see ThrowableEvents.definitionFor).
 *
 * This is what makes cross-mod consumers that query Item directly -
 * vanilla's own first-person renderer, animation mods like Not Enough
 * Animations (which Joyful Motions targets), etc. - see the correct
 * charging state. Our own event-driven throw logic in ThrowableEvents
 * is unaffected by this; it already drives the actual throw itself.
 */
@Mixin(Item.class)
public abstract class ThrowableItemMixin {

    @Inject(
            method = "getUseAnimation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/UseAnim;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void throwables$getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> cir) {
        if (ThrowableEvents.definitionFor(stack) != null) {
            cir.setReturnValue(UseAnim.SPEAR);
        }
    }

    @Inject(
            method = "getUseDuration(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void throwables$getUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (ThrowableEvents.definitionFor(stack) != null) {
            cir.setReturnValue(ThrowableEvents.MAX_USE_TICKS);
        }
    }

    @Inject(
            method = "useOnRelease(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void throwables$useOnRelease(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ThrowableEvents.definitionFor(stack) != null) {
            cir.setReturnValue(true);
        }
    }
}