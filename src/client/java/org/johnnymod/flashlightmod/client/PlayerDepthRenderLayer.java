package org.johnnymod.flashlightmod.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PlayerDepthRenderLayer {

    private static final Map<Identifier, RenderLayer> CACHE = new HashMap<>();

    public static RenderLayer get(Identifier skinTexture) {
        return CACHE.computeIfAbsent(skinTexture, PlayerDepthRenderLayer::create);
    }

    private static RenderLayer create(Identifier skinTexture) {
        return RenderLayer.of(
                "player_depth_only",
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS,
                1024,
                false,
                false,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(RenderPhase.ENTITY_SOLID_PROGRAM)
                        .texture(new RenderPhase.Texture(skinTexture, false, false))
                        .transparency(RenderPhase.NO_TRANSPARENCY)
                        // Depth only — no color buffer writes
                        .writeMaskState(RenderPhase.DEPTH_MASK)
                        .cull(RenderPhase.DISABLE_CULLING)
                        .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                        .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                        // Push depth values slightly further to prevent z-fighting
                        // with the already-rendered player geometry
                        .build(false)
        );
    }
}