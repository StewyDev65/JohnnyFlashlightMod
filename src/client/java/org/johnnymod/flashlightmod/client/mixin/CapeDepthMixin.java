package org.johnnymod.flashlightmod.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CapeFeatureRenderer.class)
public abstract class CapeDepthMixin {

    @Redirect(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private VertexConsumer redirectCapeBuffer(
            VertexConsumerProvider provider,
            RenderLayer originalLayer,
            // These are the render method params passed through to the redirect
            net.minecraft.client.util.math.MatrixStack matrices,
            VertexConsumerProvider providerArg,
            int light,
            AbstractClientPlayerEntity player,
            float f, float g, float h, float j, float k, float l
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Only swap for local player in third person
        if (player == client.player && !client.options.getPerspective().isFirstPerson()) {
            // getEntityTranslucent does not write to depth buffer —
            // cape becomes invisible to depthtex1 so it won't occlude the beam
            // We need the cape texture from the original layer's toString is unreliable,
            // so get it directly from the player
            Identifier capeTexture = player.getSkinTextures().capeTexture();
            if (capeTexture != null) {
                return provider.getBuffer(RenderLayer.getEntityTranslucent(capeTexture));
            }
        }

        return provider.getBuffer(originalLayer);
    }
}