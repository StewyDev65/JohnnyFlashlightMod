package org.johnnymod.flashlightmod;

public enum FlashlightColor {
    WHITE, WARM, RED, ORANGE, YELLOW, LIME, GREEN, TEAL,
    CYAN, LIGHT_BLUE, BLUE, PURPLE, MAGENTA, PINK, GOLD, UV;

    public String getId() { return name().toLowerCase(); }
}