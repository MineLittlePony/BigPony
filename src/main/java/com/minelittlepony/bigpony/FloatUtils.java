package com.minelittlepony.bigpony;

public class FloatUtils {

    private static final double EPSILON = .009999;

    public static boolean equals(double f1, double f2) {
        return Math.abs(f1 - f2) < EPSILON;
    }
}
