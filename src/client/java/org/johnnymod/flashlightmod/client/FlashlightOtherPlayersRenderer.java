package org.johnnymod.flashlightmod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.johnnymod.flashlightmod.FlashlightColor;
import org.johnnymod.flashlightmod.FlashlightItem;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FlashlightOtherPlayersRenderer {

    private static final int MAX_PLAYERS     = 4;
    private static final int FLOATS_PER_PLAYER = 10;
    private static final int BUFFER_SIZE     = MAX_PLAYERS * FLOATS_PER_PLAYER * 4; // 160 bytes
    private static final int SSBO_BINDING    = 1;
    private static int ourSsboId = -1;

    private static void ensureSsbo() {
        if (ourSsboId == -1) {
            int[] ids = new int[1];
            GL15.glGenBuffers(ids);
            ourSsboId = ids[0];
            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ourSsboId);
            // Create with DYNAMIC_STORAGE_BIT so we can update it
            GL44.glBufferStorage(
                    GL43.GL_SHADER_STORAGE_BUFFER,
                    BUFFER_SIZE,
                    GL44.GL_DYNAMIC_STORAGE_BIT
            );
            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
        }
    }

    private static float[] getColorRGB(FlashlightColor color) {
        return switch (color) {
            case WHITE      -> new float[]{0.85f, 0.92f, 1.00f};
            case WARM       -> new float[]{1.00f, 0.88f, 0.70f};
            case RED        -> new float[]{1.00f, 0.15f, 0.10f};
            case ORANGE     -> new float[]{1.00f, 0.50f, 0.10f};
            case YELLOW     -> new float[]{1.00f, 0.95f, 0.20f};
            case LIME       -> new float[]{0.50f, 1.00f, 0.20f};
            case GREEN      -> new float[]{0.10f, 0.80f, 0.20f};
            case TEAL       -> new float[]{0.10f, 0.80f, 0.70f};
            case CYAN       -> new float[]{0.10f, 0.90f, 1.00f};
            case LIGHT_BLUE -> new float[]{0.50f, 0.75f, 1.00f};
            case BLUE       -> new float[]{0.20f, 0.40f, 1.00f};
            case PURPLE     -> new float[]{0.60f, 0.20f, 1.00f};
            case MAGENTA    -> new float[]{1.00f, 0.20f, 0.80f};
            case PINK       -> new float[]{1.00f, 0.60f, 0.80f};
            case GOLD       -> new float[]{1.00f, 0.80f, 0.20f};
            case UV         -> new float[]{0.70f, 0.10f, 1.00f};
        };
    }

    public static void update() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Bail if Iris isn't active
        try {
            if (!net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse()) return;
        } catch (Exception e) { return; }

        Vec3d camPos = client.gameRenderer.getCamera().getPos();

        // Collect up to 4 nearby other players with flashlight on
        List<AbstractClientPlayerEntity> active = new ArrayList<>();
        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            if (active.size() >= MAX_PLAYERS) break;
            if (FlashlightItem.isOn(player.getMainHandStack()) ||
                    FlashlightItem.isOn(player.getOffHandStack())) {
                active.add(player);
            }
        }

        ByteBuffer buf = BufferUtils.createByteBuffer(BUFFER_SIZE);

        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (i < active.size()) {
                AbstractClientPlayerEntity player = active.get(i);

                // Eye position in scene space (relative to camera)
                Vec3d eye = player.getEyePos();
                buf.putFloat((float)(eye.x - camPos.x));
                buf.putFloat((float)(eye.y - camPos.y));
                buf.putFloat((float)(eye.z - camPos.z));

                // Look direction
                Vec3d look = player.getRotationVec(1.0f);
                buf.putFloat((float)look.x);
                buf.putFloat((float)look.y);
                buf.putFloat((float)look.z);

                // Color
                ItemStack held = FlashlightItem.isOn(player.getMainHandStack())
                        ? player.getMainHandStack() : player.getOffHandStack();
                float[] rgb = getColorRGB(FlashlightItem.getColor(held));
                buf.putFloat(rgb[0]).putFloat(rgb[1]).putFloat(rgb[2]);

                buf.putFloat(1.0f); // active
            } else {
                for (int j = 0; j < FLOATS_PER_PLAYER; j++) buf.putFloat(0.0f);
            }
        }
        buf.flip();

        System.out.println("FL_DEBUG: active players = " + active.size());
        if (active.size() > 0) {
            System.out.println("FL_DEBUG: player 0 isOn = " +
                    FlashlightItem.isOn(active.get(0).getMainHandStack()));
        }

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            System.out.println("FL_DEBUG: other player found: " + player.getName().getString()
                    + " mainhand: " + player.getMainHandStack().getItem()
                    + " isOn: " + FlashlightItem.isOn(player.getMainHandStack()));
        }

        // Then replace the try block in update() with:
        try {
            ensureSsbo();

            // Write our data to our own buffer
            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ourSsboId);
            GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, buf);
            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

            // Bind our buffer to binding point 1, overriding Iris's buffer
            GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, SSBO_BINDING, ourSsboId);
        } catch (Exception e) {
            System.out.println("FL_DEBUG: GL error: " + e.getMessage());
        }
    }
}