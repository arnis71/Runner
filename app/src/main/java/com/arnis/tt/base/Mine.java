package com.arnis.tt.base;

import android.util.Pair;

import com.arnis.tt.actors.Bot;
import com.arnis.tt.R;

import java.util.ArrayList;

/**
 * Created by arnis on 02.07.2016.
 */
public class Mine {
    public static ArrayList<Pair<Mine,Cell>> mines = new ArrayList<>();

    boolean active;
    boolean visible;

    public boolean isMarked() {
        return marker;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
    }

    boolean marker=false;

    public Mine(Cell cell) {
        this.active = true;
        this.visible = false;
        cell.setExploredDrawable(R.drawable.mine);
        mines.add(new Pair<Mine, Cell>(this,cell));
    }

    public Mine() {
    }

    public static void exposeAllMines(boolean onlyImage ){
        for (Pair pair:mines){
            Mine mine = (Mine) pair.first;
            if (!mine.visible) {
                mine.setMarker(true);
                mine.makeVisible((Cell) pair.second, onlyImage);
            }
        }
    }

    public static void hideAllMines(){
        for (Pair pair:mines){
            Mine mine = (Mine) pair.first;
            if (mine.isMarked()) {
                mine.makeInVisible((Cell) pair.second);
                mine.setMarker(false);
            }
        }
    }

    public void makeVisible(Cell cell,boolean onlyImage){
        cell.setImage();
        if (onlyImage){
            return;
        }
        this.visible=true;
    }

    public void makeInVisible(Cell cell){
        this.visible=false;
//        if (cell.isExplored())
//            cell.cellView.setImageResource(R.drawable.explored_cell);
//        else
            cell.applyFog();
    }

    public static Mine setMine(int chance, Cell cell){
        if (chance==1) {
            return new Mine(cell);
        }
        return null;
    }

    public static void setMine(Cell cell){
        cell.mine = new Mine();
        cell.mine.active=true;
        cell.mine.visible=true;
        cell.cellView.setImageResource(R.drawable.wall);
        mines.add(new Pair<Mine, Cell>(cell.mine,cell));
    }

    public static boolean lookFor(Bot who, Mine mine){
        if (who.isInvincible())
            return true;

        if (mine!=null) {
            if (who.smartLvl == Bot.PRIMITIVE) {
                if (mine.active && mine.visible)
                    return false;
                else return true;
            }
            if (who.smartLvl == Bot.EXPLORER){
                if (mine.active && mine.visible)
                    return false;
                else return true;
            }
            return false;
        }
        return true;
    }

    public static boolean lookFor(Cell cell){
        if (cell==null){
            return true;
        }
        if (cell.mine==null)
            return true;
        else return false;
    }
}
