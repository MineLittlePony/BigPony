package com.minelittlepony.bigpony.mod;

public class FloatUtils {

    private static final float EPSILON = .009999F;

    public static boolean equals(float f1, float f2) {
        float f3 = Math.abs(f1 - f2);
        return f3 < EPSILON;
    }
}
