package com.arnis.tt.base;

import android.util.Pair;
import android.widget.ImageView;

import com.arnis.tt.activities.Game;
import com.arnis.tt.actors.Actor;
import com.arnis.tt.actors.Bot;
import com.arnis.tt.powerUps.PowerUp;
import com.arnis.tt.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arnis on 01.07.2016.
 */
public class Cell {
    public ImageView cellView;
    public Position pos;
    public Mine mine;
    private Integer exploredDrawable;
    public PowerUp powerUp;
    private boolean explored = false;

    public Integer getExploredDrawable() {
        return exploredDrawable;
    }
    public void setExploredDrawable(int exploredDrawable) {
        this.exploredDrawable = exploredDrawable;
    }
    public void setImage(){
        this.cellView.setImageResource(getExploredDrawable());
    }
    public void setExplored(Actor actor, Game game) {
            this.explored = true;
            this.setImage();
        if (this.getPowerUp()!=null&&this.powerUp.isActive())
            powerUp.activate(this,actor, game);

    }
    public boolean isExplored() {
        return explored;
    }

    public Cell(ImageView cellView, Position pos) {
        this.cellView = cellView;
        this.pos = pos;
    }

    public void applyFog(){
        this.cellView.setImageResource(R.drawable.fog_cell);
    }
    public PowerUp getPowerUp() {
        return powerUp;
    }

    public static Integer randmomizer(int bot,Cell... cells){
        Random rnd = new Random();
        ArrayList<Pair<Integer,Cell>> cells2 = new ArrayList<>();
        boolean moveDown=false;
        int i =0;
        int sch =0;
        if (bot== Bot.EXPLORER) {
            while (i < 4) {
                if (cells[i] != null && !cells[i].isExplored()) {
                    cells2.add(new Pair<>(i + 1, cells[i]));
                    sch++;
                    if (i == 3) {
                        moveDown = true;
                    }
                }
                i++;
            }
        }
        if (bot==Bot.SAFETY_STEVE){
            while (i < 4) {
                if (cells[i] != null && cells[i].isExplored()) {
                    cells2.add(new Pair<>(i + 1, cells[i]));
                    sch++;
                    if (i == 3) {
                        moveDown = true;
                    }
                }
                i++;
            }

        }

            if (cells2.size() > 0)
                return cells2.get(rnd.nextInt(cells2.size())).first;

            else return null;

    }


}
