package io.github.hiveko.vanillafinest.fixes.weather;

/**
 * Weather module: the Weather Effect Radius option (rain and snow around the player) goes up to 15 instead of
 * Minecraft's 10. Minecraft's weather renderer keeps its column sizes in a 32 × 32 table centred on the player
 * (offset 16), which covers a radius of 15 without any other change; above that the table would overflow.
 * The vertex buffer grows by itself. Higher values in options.txt are accepted up to 15.
 */
public final class WeatherRadius {
    /** The largest radius Minecraft's 32 × 32 column table can draw. */
    public static final int MAX = 15;

    private WeatherRadius() {}
}
