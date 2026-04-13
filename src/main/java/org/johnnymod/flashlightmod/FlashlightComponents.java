package org.johnnymod.flashlightmod;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class FlashlightComponents {

    /**
     * Stored on the ItemStack to track whether the flashlight is on or off.
     * Absent = off, present (true) = on.
     */
    public static ComponentType<Boolean> FLASHLIGHT_ON;

    public static void register() {
        FLASHLIGHT_ON = Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(Flashlightmod.MOD_ID, "flashlight_on"),
                ComponentType.<Boolean>builder()
                        .codec(Codec.BOOL)
                        .packetCodec(PacketCodecs.BOOL)
                        .build()
        );
    }
}