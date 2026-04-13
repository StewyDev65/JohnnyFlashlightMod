package org.johnnymod.flashlightmod;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FlashlightCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        literal("flashlight")
                                .then(literal("color")
                                        .then(literal("set")
                                                .then(argument("color", StringArgumentType.word())
                                                        .suggests((ctx, builder) -> {
                                                            for (FlashlightColor c : FlashlightColor.values())
                                                                builder.suggest(c.getId());
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ctx -> {
                                                            String colorName = StringArgumentType.getString(ctx, "color");
                                                            FlashlightColor color;
                                                            try {
                                                                color = FlashlightColor.valueOf(colorName.toUpperCase());
                                                            } catch (IllegalArgumentException e) {
                                                                ctx.getSource().sendError(
                                                                        Text.literal("Unknown color: " + colorName));
                                                                return 0;
                                                            }

                                                            var player = ctx.getSource().getPlayerOrThrow();

                                                            // Check both hands for a flashlight
                                                            for (Hand hand : Hand.values()) {
                                                                ItemStack stack = player.getStackInHand(hand);
                                                                if (FlashlightItem.isOn(stack)) {
                                                                    // Swap to same color on variant
                                                                    player.setStackInHand(hand,
                                                                            new ItemStack(Flashlightmod.FLASHLIGHT_ON_ITEMS.get(color)));
                                                                    ctx.getSource().sendFeedback(() ->
                                                                            Text.literal("Flashlight color set to " + color.getId()), false);
                                                                    return 1;
                                                                }
                                                                if (stack.isOf(Flashlightmod.FLASHLIGHT_OFF)) {
                                                                    // Update stored color on off item
                                                                    FlashlightItem.setStoredColor(stack, color);
                                                                    ctx.getSource().sendFeedback(() ->
                                                                            Text.literal("Flashlight color set to " + color.getId()), false);
                                                                    return 1;
                                                                }
                                                            }

                                                            ctx.getSource().sendError(
                                                                    Text.literal("You must be holding a flashlight."));
                                                            return 0;
                                                        })
                                                )
                                        )
                                )
                )
        );
    }
}