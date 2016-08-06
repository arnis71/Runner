package com.arnis.tt.actors;

import android.widget.ImageView;

import com.arnis.tt.base.Position;
import com.arnis.tt.base.Table;

/**
 * Created by arnis on 01.07.2016.
 */
public class Player extends Actor {

    public int getDirection(boolean autoMove) {
        if (autoMove)
            return direction;
        else {
            int buf = direction;
            direction=0;
            return buf;
        }
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private int direction;
    public Thread walking;
    Table table;



    public Player(ImageView look, Position pos, Table table) {
        this.look = look;
        this.pos = pos;
        this.direction = 0;
        this.table = table;


    }

    public void startWalking(final Boolean autoMoving){
        final Player player = this;
        walking = new Thread(new Runnable() {
            @Override
            public void run() {
                while (player.isAlive()&&!player.isFinished()&&!walking.isInterrupted())
                    try {
                        Thread.sleep(player.getSpeed());
                        if (!player.isFrozen()&&player.isAlive()&&!player.isFinished()) {
                            table.game.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    table.moveActor(player, player.getDirection(autoMoving));
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        });
        walking.start();
    }

    public int getBonus(){
        return this.bonus;
    }

    public void useBonus(int bonus) {
        this.bonus -=bonus;
    }
}
