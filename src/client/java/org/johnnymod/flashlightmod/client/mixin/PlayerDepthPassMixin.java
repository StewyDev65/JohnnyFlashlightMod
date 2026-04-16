package org.johnnymod.flashlightmod.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.johnnymod.flashlightmod.client.PlayerDepthRenderLayer;

@Mixin(LivingEntityRenderer.class)
public abstract class PlayerDepthPassMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow protected M model;
    @Shadow protected abstract void render(T entity, float yaw, float tickDelta,
                                           MatrixStack matrices, VertexConsumerProvider provider, int light);

    // Capture the entity reference so we can use it in the model.render inject
    private T flashlight_currentEntity;
    private VertexConsumerProvider flashlight_currentProvider;
    private int flashlight_currentLight;

    // Capture entity/provider at HEAD of render
    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD")
    )
    private void captureRenderArgs(T entity, float yaw, float tickDelta,
                                   MatrixStack matrices, VertexConsumerProvider provider,
                                   int light, CallbackInfo ci) {
        flashlight_currentEntity   = entity;
        flashlight_currentProvider = provider;
        flashlight_currentLight    = light;
    }

    // Inject RIGHT AFTER model.render() — matrices are still fully set up here
    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void renderDepthPass(T entity, float yaw, float tickDelta,
                                 MatrixStack matrices, VertexConsumerProvider provider,
                                 int light, CallbackInfo ci) {

        if (!(flashlight_currentEntity instanceof AbstractClientPlayerEntity player)) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Skip for local player in third person — body would shadow their own beam
        if (player == client.player && !client.options.getPerspective().isFirstPerson()) {
            return;
        }

        VertexConsumer consumer = flashlight_currentProvider.getBuffer(
                PlayerDepthRenderLayer.get(player.getSkinTextures().texture())
        );

        model.render(matrices, consumer, flashlight_currentLight,
                net.minecraft.client.render.OverlayTexture.DEFAULT_UV, -1);
    }
}