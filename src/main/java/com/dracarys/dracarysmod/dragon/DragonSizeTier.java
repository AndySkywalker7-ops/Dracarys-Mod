package com.dracarys.dracarysmod.dragon;
public enum DragonSizeTier {
    SMALL(6,15,1.0f), MEDIUM(15,30,1.55f), LARGE(30,50,2.35f), GIANT(50,100,3.60f);
    public final int minLength, maxLength; public final float power;
    DragonSizeTier(int minLength,int maxLength,float power){this.minLength=minLength;this.maxLength=maxLength;this.power=power;}
    public static DragonSizeTier byId(int id){DragonSizeTier[] v=values();return v[Math.max(0,Math.min(v.length-1,id))];}
    public static DragonSizeTier byName(String n){for(var v:values())if(v.name().equalsIgnoreCase(n))return v;return SMALL;}
}
