package com.auy.throwables;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Throwables.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(
        modid = Throwables.MOD_ID,
        value = Dist.CLIENT
)
public final class ThrowableClient {

    public ThrowableClient(ModContainer container) {
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (minecraft, parent) ->
                        new ConfigurationScreen(container, parent)
        );
    }

    @SubscribeEvent
    public static void registerClientExtensions(
            RegisterClientExtensionsEvent event
    ) {
        IClientItemExtensions throwableAnimation =
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

                    @Override
                    public boolean applyForgeHandTransform(
                            PoseStack poseStack,
                            LocalPlayer player,
                            HumanoidArm arm,
                            ItemStack itemInHand,
                            float partialTick,
                            float equipProcess,
                            float swingProcess
                    ) {
                        if (
                                !player.isUsingItem()
                                        || player.getUseItem().isEmpty()
                        ) {
                            return false;
                        }

                        InteractionHand usedHand =
                                player.getUsedItemHand();

                        HumanoidArm usedArm =
                                usedHand == InteractionHand.MAIN_HAND
                                        ? player.getMainArm()
                                        : player.getMainArm().getOpposite();

                        if (arm != usedArm) {
                            return false;
                        }

                        float side =
                                arm == HumanoidArm.RIGHT
                                        ? 1.0F
                                        : -1.0F;

                        /*
                         * Keep the throwable raised, but smaller and
                         * farther toward the side of the screen.
                         */
                        poseStack.translate(
                                side * 0.28F,
                                -0.02F,
                                -0.18F
                        );

                        /*
                         * Smaller first-person item.
                         */
                        poseStack.scale(
                                0.60F,
                                0.60F,
                                0.60F
                        );

                        /*
                         * Raised throwing angle.
                         */
                        poseStack.mulPose(
                                Axis.XP.rotationDegrees(-55.0F)
                        );

                        poseStack.mulPose(
                                Axis.YP.rotationDegrees(
                                        side * 18.0F
                                )
                        );

                        poseStack.mulPose(
                                Axis.ZP.rotationDegrees(
                                        side * 6.0F
                                )
                        );

                        return true;
                    }
                };

        event.registerItem(
                throwableAnimation,
                Items.EGG,
                Items.SNOWBALL,
                Items.ENDER_PEARL,
                Items.EXPERIENCE_BOTTLE,
                Items.SPLASH_POTION,
                Items.LINGERING_POTION
        );
    }
}