package com.arnis.tt.powerUps;

import com.arnis.tt.actors.Actor;
import com.arnis.tt.base.Cell;
import com.arnis.tt.activities.Game;

/**
 * Created by arnis on 05.07.2016.
 */
public class Mining extends PowerUp {


    public Mining() {
        this.duration=5000;
        this.setActive(true);

    }

    @Override
    public void activate(Cell cell, Actor actor, Game game) {
        if (actor.crown!=null){
            duration+= 1000;
        }
        //cell.cellView.setImageResource(R.drawable.wall);
        this.setVisible(true);
        cell.getPowerUp().setActive(false);
//        Game.MP.getMP(game,R.raw.freeze).start();
        actor.setMining(true,duration);
    }
}
