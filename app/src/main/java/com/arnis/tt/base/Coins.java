package com.arnis.tt.base;

import android.content.SharedPreferences;

/**
 * Created by arnis on 10.07.2016.
 */
public class Coins {

    public static final String COINS_DB = "data";
    public static final String COINS_AMOUNT = "snoic";
    public static final int COIN_SHORTAGE_RATE = 10;
    private int amount;
    private SharedPreferences coinsPrefs;

    public Coins(SharedPreferences sharedPreferences) {
        amount = sharedPreferences.getInt(COINS_AMOUNT,0);
        coinsPrefs = sharedPreferences;
    }

    public void assignCoins(int amount){
        this.amount+=amount/COIN_SHORTAGE_RATE;
        coinsPrefs.edit().putInt(COINS_AMOUNT,this.amount).apply();

    }


    public int getAmount() {
        return amount;
    }

    public void buy(int cost) {
        amount-=cost;
        coinsPrefs.edit().putInt(COINS_AMOUNT,this.amount).apply();

    }
}
