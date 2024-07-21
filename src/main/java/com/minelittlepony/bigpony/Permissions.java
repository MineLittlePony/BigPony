package com.minelittlepony.bigpony;

public interface Permissions {
    long DEFAULT = pack(false, true, true);

    static boolean hitbox(long bits) {
        return (bits & 0b001) != 0;
    }

    static boolean camera(long bits) {
        return (bits & 0b010) != 0;
    }

    static boolean freeform(long bits) {
        return (bits & 0b100) != 0;
    }

    static long pack(boolean hitbox, boolean camera, boolean freeform) {
        return ((hitbox ? 1 : 0) << 0) | ((camera ? 1 : 0) << 1) | ((freeform ? 1 : 0) << 2);
    }
}
