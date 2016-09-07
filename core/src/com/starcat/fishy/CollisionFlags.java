package com.starcat.fishy;

/**
 * Created by Vincent on 9/4/2016.
 */
public class CollisionFlags {
    public static final short FISH_FLAG = 1<<2;
    public static final short ENEMY_FISH_FLAG = 1<<3;
    public static final short GROUND_FLAG = 1<<4;

    public static final short FISH_MASK = ENEMY_FISH_FLAG | GROUND_FLAG;
    public static final short ENEMY_FISH_MASK = FISH_FLAG | GROUND_FLAG;
    public static final short GROUND_MASK = FISH_FLAG;

}
