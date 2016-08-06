package com.arnis.tt.powerUps;

import com.arnis.tt.activities.Game;
import com.arnis.tt.actors.Actor;
import com.arnis.tt.base.Cell;

import java.util.Random;

/**
 * Created by arnis on 10.07.2016.
 */
public class Spring extends PowerUp {
    public Spring() {
        this.duration=7;
        this.setActive(true);
    }

    @Override
    public void activate(Cell cell, Actor actor, Game game) {
        if (actor.crown!=null) {
            duration-=2;
        }
        //cell.cellView.setImageResource(R.drawable.spring);
        this.setVisible(true);
        cell.getPowerUp().setActive(false);
        //Game.MP.getMP(game,R.raw.freeze).start();
        Random rnd = new Random();
        int i,j;
        do{
            i = actor.pos.row+rnd.nextInt(duration)-3;
            j = actor.pos.col+rnd.nextInt(duration)-3;

        }while(game.table.getFromCells(i,j)==null&&i!=actor.pos.row&&j!=actor.pos.col);
        Cell cel = game.table.getFromCells(i,j);
        game.table.moveActor(actor,cel);

    }
}
