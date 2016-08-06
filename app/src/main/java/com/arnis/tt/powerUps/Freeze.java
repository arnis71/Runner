package com.arnis.tt.powerUps;

import com.arnis.tt.actors.Actor;
import com.arnis.tt.actors.Bot;
import com.arnis.tt.base.Cell;
import com.arnis.tt.activities.Game;
import com.arnis.tt.actors.Player;
import com.arnis.tt.costumes.AntiFreeze;
import com.arnis.tt.R;
import com.arnis.tt.base.Table;

/**
 * Created by arnis on 04.07.2016.
 */
public class Freeze extends PowerUp {


    public Freeze() {
        this.duration = 3000;//in ms
        this.setActive(true);

    }

    @Override
    public void activate(Cell cell, Actor actor,Game game) {
        if (actor.crown!=null){
            duration+= 1000;
        }
        if (actor.getCostume()!=null&&actor.getCostume() instanceof AntiFreeze)
            duration+=1000;
        //cell.cellView.setImageResource(R.drawable.time_freeze);
        this.setVisible(true);
        cell.getPowerUp().setActive(false);
        Game.MP.getMP(game,R.raw.freeze).start();
        if (actor instanceof Player){
            for (Bot b:Bot.bots){
                if (!b.isFinished()&&b.isAlive()&&!b.isInvincible()){
                    b.freeze(duration,cell);

                }
            }
        }

        if (actor instanceof Bot){
            for (Bot b:Bot.bots){
                if (b.hashCode()!=actor.hashCode()&&!b.isFinished()&&b.isAlive()&&!b.isInvincible()){
                    b.freeze(duration,cell);

                }
            }
            Table.getPlayer().freeze(duration,cell);
        }
    }
}
