package com.minelittlepony.bigpony;

public interface Permissions {
    long DEFAULT = pack(false, true, true);

    int FLAG_HITBOX = 0b001;
    int FLAG_CAMERA = 0b010;
    int FLAG_FREEFORM = 0b100;

    static long with(long bits, int flag, boolean value) {
        return value ? (bits | flag) : (bits & ~flag);
    }

    static boolean getFlag(long bits, int flag) {
        return (bits & flag) != 0;
    }

    static boolean hitbox(long bits) {
        return getFlag(bits, FLAG_HITBOX);
    }

    static boolean camera(long bits) {
        return getFlag(bits, FLAG_CAMERA);
    }

    static boolean freeform(long bits) {
        return getFlag(bits, FLAG_FREEFORM);
    }

    static long pack(boolean hitbox, boolean camera, boolean freeform) {
        return with(with(with(0, FLAG_HITBOX, hitbox), FLAG_CAMERA, camera), FLAG_FREEFORM, freeform);
    }
}
