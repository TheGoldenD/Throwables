package com.auy.throwables;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(
        modid = Throwables.MOD_ID,
        value = Dist.CLIENT
)
public final class ThrowableClient {

    private ThrowableClient() {
    }

    @SubscribeEvent
    public static void registerClientExtensions(
            RegisterClientExtensionsEvent event
    ) {
        IClientItemExtensions spearAnimation =
                new IClientItemExtensions() {

                    @Override
                    public HumanoidModel.ArmPose getArmPose(
                            LivingEntity entity,
                            InteractionHand hand,
                            ItemStack stack
                    ) {
                        if (
                                entity.isUsingItem()
                                        && entity.getUsedItemHand() == hand
                        ) {
                            return HumanoidModel.ArmPose.THROW_SPEAR;
                        }

                        return null;
                    }
                };

        event.registerItem(
                spearAnimation,
                Items.EGG,
                Items.SNOWBALL
        );
    }
}