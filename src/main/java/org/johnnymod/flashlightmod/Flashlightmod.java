package org.johnnymod.flashlightmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

public class Flashlightmod implements ModInitializer {

    public static final String MOD_ID = "flashlight";

    public static final FlashlightItem FLASHLIGHT_OFF = new FlashlightItem(
            new Item.Settings().maxCount(1)
    );

    public static final Map<FlashlightColor, FlashlightItem> FLASHLIGHT_ON_ITEMS =
            new EnumMap<>(FlashlightColor.class);

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM,
                Identifier.of(MOD_ID, "flashlight_off"), FLASHLIGHT_OFF);

        for (FlashlightColor color : FlashlightColor.values()) {
            FlashlightItem item = new FlashlightItem(new Item.Settings().maxCount(1));
            FLASHLIGHT_ON_ITEMS.put(color, item);
            Registry.register(Registries.ITEM,
                    Identifier.of(MOD_ID, "flashlight_on_" + color.getId()), item);
        }

        FlashlightCommand.register();
    }
}