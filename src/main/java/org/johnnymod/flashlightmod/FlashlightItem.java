package org.johnnymod.flashlightmod;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Map;

public class FlashlightItem extends Item {

    public FlashlightItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        boolean currentlyOn = isOn(stack);

        ItemStack newStack;
        if (currentlyOn) {
            // Turn off — store current color on the off item
            FlashlightColor color = getColor(stack);
            newStack = new ItemStack(Flashlightmod.FLASHLIGHT_OFF);
            setStoredColor(newStack, color);
        } else {
            // Turn on — restore last color
            FlashlightColor color = getStoredColor(stack);
            newStack = new ItemStack(Flashlightmod.FLASHLIGHT_ON_ITEMS.get(color));
        }

        if (!world.isClient) {
            world.playSound(null, user.getBlockPos(),
                    SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS,
                    0.3f, currentlyOn ? 0.8f : 1.2f);
            user.sendMessage(
                    Text.literal("Flashlight " + (currentlyOn ? "OFF" : "ON")), true);
        }

        return TypedActionResult.success(newStack);
    }

    public static boolean isOn(ItemStack stack) {
        return Flashlightmod.FLASHLIGHT_ON_ITEMS.containsValue(stack.getItem());
    }

    public static FlashlightColor getColor(ItemStack stack) {
        for (Map.Entry<FlashlightColor, FlashlightItem> entry :
                Flashlightmod.FLASHLIGHT_ON_ITEMS.entrySet()) {
            if (stack.isOf(entry.getValue())) return entry.getKey();
        }
        return FlashlightColor.WARM; // changed
    }

    public static FlashlightColor getStoredColor(ItemStack stack) {
        NbtComponent nbt = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) return FlashlightColor.WARM;
        String name = nbt.copyNbt().getString("flashlight_color");
        try { return FlashlightColor.valueOf(name.toUpperCase()); }
        catch (IllegalArgumentException e) { return FlashlightColor.WARM; }
    }

    public static void setStoredColor(ItemStack stack, FlashlightColor color) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("flashlight_color", color.getId());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}