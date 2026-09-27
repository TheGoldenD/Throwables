package com.auy.throwables.compat;

import com.auy.throwables.ThrowableEvents;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;

import net.minecraft.world.entity.LivingEntity;

/**
 * Loaded reflectively by Shoulder Surfing Reloaded itself (via
 * shouldersurfing_plugin.json) if and only if it is installed.
 * Nothing in the rest of the mod references this class directly,
 * so it is never classloaded on a setup without Shoulder Surfing.
 */
public class ThrowableShoulderSurfingPlugin implements IShoulderSurfingPlugin {

    @Override
    public void register(IEventBus eventBus) {
        eventBus.register((ComputePlayerAimStateEvent event) -> {
            if (event.getResult()) {
                return; // some other mod already said "yes, aiming" - don't override
            }

            LivingEntity entity = event.getEntity();

            if (
                    entity.isUsingItem()
                            && ThrowableEvents.definitionFor(entity.getUseItem()) != null
            ) {
                event.setResult(true);
            }
        });
    }
}