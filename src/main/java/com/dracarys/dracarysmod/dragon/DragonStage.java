package com.dracarys.dracarysmod.dragon;
public enum DragonStage {
    BABY(0.20f), JUVENILE(0.35f), ADOLESCENT(0.55f), ADULT(0.75f), ANCIENT(0.90f), COLOSSAL(1.00f);
    private final float growth;
    DragonStage(float growth){this.growth=growth;}
    public float growth(){return growth;}
    public static DragonStage byId(int id){DragonStage[] v=values(); return v[Math.max(0,Math.min(v.length-1,id))];}
    public DragonStage next(){return byId(ordinal()+1);}
}
