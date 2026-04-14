package org.johnnymod.flashlightmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class FlashlightmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDeltaManager) ->
                FlashlightOtherPlayersRenderer.update()
        );
    }
}
