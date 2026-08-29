package com.dracarys.dracarysmod.dragon;

public enum DragonVariant {
    BLACK("black", 0x242424),
    WHITE("white", 0xE8E8E8),
    GRAY("gray", 0x777777),
    RED("red", 0xB52A23),
    CRIMSON("crimson", 0x7E1831),
    ORANGE("orange", 0xE36D24),
    GOLD("gold", 0xD9A928),
    GREEN("green", 0x3F8A45),
    DARK_GREEN("dark_green", 0x205A35),
    BLUE("blue", 0x3268C8),
    DARK_BLUE("dark_blue", 0x203F80),
    TURQUOISE("turquoise", 0x2AA6A6),
    PURPLE("purple", 0x7E4AB1),
    SILVER("silver", 0xAEB7C4),
    BROWN("brown", 0x6F4A2F);
    private final String id;
    private final int color;
    DragonVariant(String id, int color) { this.id = id; this.color = color; }
    public String id() { return id; }
    public int color() { return color; }
    public static DragonVariant byId(int id) { DragonVariant[] v=values(); return v[Math.floorMod(id, v.length)]; }
    public static DragonVariant byName(String name) { for (DragonVariant v: values()) if(v.id.equalsIgnoreCase(name)) return v; return RED; }
}
