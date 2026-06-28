package com.corrodinggames.rts.gameFramework.graphics;

import io.github.rwx.render.canvas.KoolArgbColor;

public final class TeamColoring {
    private TeamColoring() {
    }

    public static int pureGreen(int color, int teamColor) {
        int alpha = KoolArgbColor.a(color);
        if (alpha == 0) {
            return color == 0 ? color : 0;
        }
        int green = KoolArgbColor.c(color);
        int red = KoolArgbColor.b(color);
        if (green <= 0 || red != KoolArgbColor.d(color)) {
            return color;
        }
        int teamRed = KoolArgbColor.b(teamColor);
        int teamGreen = KoolArgbColor.c(teamColor);
        int teamBlue = KoolArgbColor.d(teamColor);
        if (red == 0) {
            return KoolArgbColor.a(alpha,
                    (teamRed * green) >> 8,
                    (teamGreen * green) >> 8,
                    (teamBlue * green) >> 8);
        }
        if (green != red) {
            float amount = (green * 0.003921569f) - (red * 0.003921569f);
            return KoolArgbColor.a(alpha,
                    clamp((int) (red + (teamRed * amount))),
                    clamp((int) (red + (teamGreen * amount))),
                    clamp((int) (red + (teamBlue * amount))));
        }
        return color;
    }

    public static int hueShift(int color, int teamColor) {
        int alpha = KoolArgbColor.a(color);
        if (alpha == 0) {
            return KoolArgbColor.b(color) > 0 || KoolArgbColor.c(color) > 0 || KoolArgbColor.d(color) > 0 ? 0 : color;
        }
        int red = KoolArgbColor.b(color);
        int green = KoolArgbColor.c(color);
        int blue = KoolArgbColor.d(color);
        float min = Math.min(Math.min(red, green), blue);
        float maxDifference = Math.max(Math.max(Math.abs(red - green), Math.abs(green - blue)), Math.abs(blue - red));
        if (maxDifference <= 15.0f) {
            return color;
        }
        float amount = maxDifference / 255.0f;
        return KoolArgbColor.a(alpha,
                clamp((int) (min + (KoolArgbColor.b(teamColor) * amount))),
                clamp((int) (min + (KoolArgbColor.c(teamColor) * amount))),
                clamp((int) (min + (KoolArgbColor.d(teamColor) * amount))));
    }

    public static int hueAdd(int color, int teamColor) {
        int alpha = KoolArgbColor.a(color);
        if (alpha <= 0) {
            return color;
        }
        return KoolArgbColor.a(alpha,
                clamp((int) (KoolArgbColor.b(color) + (KoolArgbColor.b(teamColor) * 0.15f))),
                clamp((int) (KoolArgbColor.c(color) + (KoolArgbColor.c(teamColor) * 0.15f))),
                clamp((int) (KoolArgbColor.d(color) + (KoolArgbColor.d(teamColor) * 0.15f))));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
