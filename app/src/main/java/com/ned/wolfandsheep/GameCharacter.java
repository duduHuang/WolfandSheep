package com.ned.wolfandsheep;

/**
 * Created by NedHuang on 2016/8/29.
 */
public class GameCharacter {
    protected String[] werewolf = new String[2];
    protected String[] villager = new String[3];
    protected String minion;
    protected String robber;
    protected String drunk;
    protected String troublemaker;
    protected String seer;
    protected String insomniac;
    protected String hunter;
    protected String tanner;
    protected String[] mason = new String[2];
    protected String doppelganger;

    protected void initialize() {
        for (int i = 0; i < werewolf.length; i++) {
            werewolf[i] = null;
            mason[i] = null;
        }
        for (int i = 0; i < villager.length; i++) {
            villager[i] = null;
        }
    }
}
