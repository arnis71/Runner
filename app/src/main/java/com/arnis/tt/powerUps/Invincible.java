package com.arnis.tt.powerUps;

import com.arnis.tt.actors.Actor;
import com.arnis.tt.base.Cell;
import com.arnis.tt.activities.Game;
import com.arnis.tt.R;

/**
 * Created by arnis on 06.07.2016.
 */
public class Invincible extends PowerUp{

    public Invincible() {
        this.duration=5000;
        this.setActive(true);
    }

    @Override
    public void activate(Cell cell, Actor actor, Game game) {
        if (actor.crown!=null){
            duration+= 1000;
        }
        //cell.cellView.setImageResource(R.drawable.invincible_powerup);
        this.setVisible(true);
        cell.getPowerUp().setActive(false);
        actor.setLook(R.mipmap.invivncible);
        actor.setInvincible(true,this.duration,actor);
        //Game.MP.getMP(game,R.raw.freeze).start();
    }
}
