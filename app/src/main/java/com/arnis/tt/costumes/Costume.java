package com.arnis.tt.costumes;

import android.content.SharedPreferences;
import android.view.View;

import com.arnis.tt.activities.Menu;
import com.arnis.tt.activities.MyPassport;
import com.arnis.tt.actors.Player;
import com.arnis.tt.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by arnis on 10.07.2016.
 */
public abstract class Costume {
    public static final int BASIC = 1;
    public static final int ANTI_FREEZE = 2;
    public static final int POWERUP_SENSE = 3;
    public static final int BOMB_SENSE = 4;

    public static final String AVALIABLE_COSTUMES_DB = "avac";
    public static final String COSTUMES_DB = "semutoc";
    public static final String CURRENT_COSTUME  = "current_costume";

    public int id;

    public int getDrawableID() {
        return drawableID;
    }

    public View look;
    int drawableID;
    int cost;
    boolean avaliable;
    static Costume lastActive= null;

    public static Costume getActiveCostume() {
        return activeCostume;
    }

    public static void setActiveCostume(Costume activeCostume) {
        Costume.activeCostume = activeCostume;
    }

    public static void dressPlayer(Player player){
        player.setCostume(activeCostume);
        player.look.setImageResource(activeCostume.getDrawableID());
    }

    private static Costume activeCostume = null;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active){
            activeCostume=this;
            if (activeCostume.look!=null)
                activeCostume.look.findViewById(R.id.costume_check_current_active).setVisibility(View.VISIBLE);
            if (lastActive!=null){
                lastActive.setActive(false);
                if (lastActive.look!=null)
                    lastActive.look.findViewById(R.id.costume_check_current_active).setVisibility(View.INVISIBLE);
            }
            lastActive = this;
        }
    }

    boolean active;

    public static ArrayList<Costume> costumes;

    public boolean isAvaliable() {
        return avaliable;
    }

    public void setAvaliable(boolean avaliable) {
        this.avaliable = avaliable;
        if (this instanceof AntiFreeze)
            this.drawableID = R.drawable.ice_unlocked;
        if (this instanceof PowerUpSense)
            this.drawableID = R.drawable.powerup_unlocked;
        if (this instanceof  BombSense)
            this.drawableID = R.drawable.bomb_unlocked;
    }


    public static void initCostumes(){
        costumes = new ArrayList<>();
        costumes.add(new Basic());
        costumes.add(new AntiFreeze());
        costumes.add(new PowerUpSense());
        costumes.add(new BombSense());
    }

    public static void setActive(SharedPreferences sharedPreferences){
        int activeID = sharedPreferences.getInt(CURRENT_COSTUME,BASIC);
        for (Costume c: costumes){
            if(c.id == activeID)
                c.setActive(true);
        }
    }

    public static void setAvaliable(SharedPreferences sharedPreferences){
        //sharedPreferences.edit().clear().apply();
        Map<String, ?> map = sharedPreferences.getAll();
        for (Map.Entry<String,?> entry:map.entrySet()){
            if (entry.getKey().contains(AVALIABLE_COSTUMES_DB)){
                int pos = ((Integer) entry.getValue())-1;
                costumes.get(pos).setAvaliable(true);
            }
        }
//        Iterator<String> iterator = set.iterator();
//        if (iterator.hasNext()){
//            costumes.get(Integer.parseInt(iterator.next())).setAvaliable(true);
//
//        }

    }

    public void purchase(SharedPreferences sharedPreferences) {
        if (!(this instanceof Basic) && MyPassport.coins.getAmount()>=this.cost){
            MyPassport.coins.buy(this.cost);
            this.setAvaliable(true);
            this.setActive(true);
        }
        sharedPreferences.edit().putInt(AVALIABLE_COSTUMES_DB +Integer.toString(this.id),this.id).apply();

    }
}
