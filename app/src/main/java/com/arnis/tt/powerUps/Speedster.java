package com.arnis.tt.powerUps;

import com.arnis.tt.actors.Actor;
import com.arnis.tt.base.Cell;
import com.arnis.tt.activities.Game;

/**
 * Created by arnis on 07.07.2016.
 */
public class Speedster extends PowerUp {
    public Speedster() {
        this.duration = 100;//in ms
        this.setActive(true);

    }

    @Override
    public void activate(Cell cell, Actor actor, Game game) {
        if (actor.crown!=null){
            duration+= 50;
        }
        //cell.cellView.setImageResource(R.drawable.speedster);
        this.setVisible(true);
        cell.getPowerUp().setActive(false);
        actor.setSpeed(actor.getSpeed()-duration);
        //Game.MP.getMP(game,R.raw.freeze).start();

    }
}
