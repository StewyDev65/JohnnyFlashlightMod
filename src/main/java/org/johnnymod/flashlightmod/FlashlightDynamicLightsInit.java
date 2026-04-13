package org.johnnymod.flashlightmod;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Range;

public class FlashlightDynamicLightsInit implements DynamicLightsInitializer {

    /**
     * Registered type for our luminance provider.
     * Must be a static field on the initializer class so it exists
     * before getLuminance() is ever called.
     */
    public static final EntityLuminance.Type FLASHLIGHT_LUMINANCE_TYPE =
            EntityLuminance.Type.registerSimple(
                    Identifier.of(Flashlightmod.MOD_ID, "flashlight_holder"),
                    FlashlightHolderLuminance.INSTANCE
            );

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        context.entityLightSourceManager().onRegisterEvent().register(ctx -> {
            ctx.register(EntityType.PLAYER, FlashlightHolderLuminance.INSTANCE);
        });
    }

    // Deprecated overload — must implement to satisfy compiler, leave empty
    @Override
    @Deprecated
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {
        // scheduled for removal in 4.0.0+1.21.4, intentionally empty
    }

    // -------------------------------------------------------------------------
    // Inner luminance provider — checks if the player is holding an active
    // flashlight and returns luminance 15 if so, 0 otherwise.
    // -------------------------------------------------------------------------

    public static final class FlashlightHolderLuminance implements EntityLuminance {

        public static final FlashlightHolderLuminance INSTANCE = new FlashlightHolderLuminance();

        private FlashlightHolderLuminance() {}

        @Override
        public Type type() {
            return FLASHLIGHT_LUMINANCE_TYPE;
        }

        @Override
        public @Range(from = 0, to = 15) int getLuminance(
                ItemLightSourceManager itemLightSourceManager,
                Entity entity
        ) {
            if (!(entity instanceof PlayerEntity player)) return 0;

            // Check main hand first, then offhand
            ItemStack mainhand = player.getMainHandStack();
            if (FlashlightItem.isOn(mainhand)) return 15;

            ItemStack offhand = player.getOffHandStack();
            if (FlashlightItem.isOn(offhand)) return 15;

            return 0;
        }
    }
}