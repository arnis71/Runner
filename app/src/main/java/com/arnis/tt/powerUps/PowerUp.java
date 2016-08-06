package com.arnis.tt.powerUps;

import android.util.Pair;

import com.arnis.tt.activities.Game;
import com.arnis.tt.actors.Actor;
import com.arnis.tt.base.Cell;
import com.arnis.tt.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arnis on 04.07.2016.
 */
public abstract class PowerUp {

    public static ArrayList<Pair<PowerUp,Cell>> pus = new ArrayList<>();

    int duration;

    public static void exposeAllPowerups(boolean autoDismiss){
        for (Pair pair: pus){
            Cell cell;
            cell = ((Cell)pair.second);
            if (!cell.getPowerUp().isVisible()) {
                cell.getPowerUp().setVisible(true);
                if (pair.first instanceof Freeze)
                    cell.cellView.setImageResource(R.drawable.time_freeze);
                else if (pair.first instanceof Mining)
                    cell.cellView.setImageResource(R.drawable.wall);
                else if (pair.first instanceof Invincible)
                    cell.cellView.setImageResource(R.drawable.invincible_powerup);
                else if (pair.first instanceof Spring)
                    cell.cellView.setImageResource(R.drawable.spring);
                else if (pair.first instanceof Speedster)
                    cell.cellView.setImageResource(R.drawable.speedster);
            }

        }

        if (autoDismiss) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    hideAllPowerups();

                }
            }).start();
        }
    }

    public static void hideAllPowerups(){
        for (Pair pair:pus){
            PowerUp pu = ((PowerUp)pair.first);
            final Cell cell = ((Cell)pair.second);
            if (pu.isActive()){
                pu.setVisible(false);
                cell.cellView.post(new Runnable() {
                    @Override
                    public void run() {
                        cell.cellView.setImageResource(R.drawable.fog_cell);
                    }
                });
            }
//            else {
//                pu.setVisible(true);
//                cell.cellView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        cell.cellView.setImageResource(R.drawable.explored_cell);
//                    }
//                });
//
//            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    boolean active = false;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    boolean visible = false;

    public static PowerUp setPowerUp(Cell cell, int chanceOfPowerUp){

        if (chanceOfPowerUp==1) {
            Random random = new Random();
            int temp =random.nextInt(5)+1;
            if (temp==1){
                Freeze freeze= new Freeze();
                cell.setExploredDrawable(R.drawable.time_freeze);
                pus.add(new Pair<PowerUp, Cell>(freeze,cell));
                return freeze;
            }
            else if(temp==2){
                Mining mining = new Mining();
                cell.setExploredDrawable(R.drawable.wall);
                pus.add(new Pair<PowerUp, Cell>(mining,cell));
                return mining;
            }
            else if(temp==3){
                Spring spring= new Spring();
                cell.setExploredDrawable(R.drawable.spring);
                pus.add(new Pair<PowerUp, Cell>(spring,cell));
                return spring;
            }
            else if (temp==4) {
                Invincible invincible = new Invincible();
                cell.setExploredDrawable(R.drawable.invincible_powerup);
                pus.add(new Pair<PowerUp, Cell>(invincible,cell));
                return invincible;
            }
            else {
                Speedster speedster = new Speedster();
                cell.setExploredDrawable(R.drawable.speedster);
                pus.add(new Pair<PowerUp, Cell>(speedster,cell));
                return speedster;
            }
        }



        return null;
    }

    public void activate(Cell cell, Actor actor, Game game){};




}
