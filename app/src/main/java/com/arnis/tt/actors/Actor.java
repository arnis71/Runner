package com.arnis.tt.actors;

import android.animation.Animator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;

import com.arnis.tt.activities.Game;
import com.arnis.tt.base.Cell;
import com.arnis.tt.costumes.AntiFreeze;
import com.arnis.tt.costumes.Costume;
import com.arnis.tt.base.Position;
import com.arnis.tt.R;
import com.arnis.tt.base.Table;

/**
 * Created by arnis on 01.07.2016.
 */
public abstract class Actor {
    public ImageView getLook() {
        return look;
    }

    public void setLook(int id) {
        this.look.setImageResource(id);
    }

    public ImageView look;
    public ImageView crown;
    public Position pos;
    public static boolean crownExists =false;



    public Costume getCostume() {
        return costume;
    }

    public void setCostume(Costume costume) {
        this.costume = costume;
    }

    private Costume costume;

//    public void giveCrown(Game game, float y){
//        if (crownExists){
//            if (this.crown!=null)
//                crown.animate()
//                        .x(this.pos.row)
//                        .y(y)
//                        .setDuration(300);
//            else {
//                this.crown
//            }
//        }else {
//            crownExists=true;
//            this.addCrown(game,y);
//        }
//    }

    public void addCrown(final Game game, final float y, int cellsize){
        crown = new ImageView(game);
        crown.setLayoutParams(new RelativeLayout.LayoutParams(cellsize, cellsize));
        crown.setImageResource(R.drawable.crown);
        game.mainLayout.post(new Runnable() {
            @Override
            public void run() {
                game.mainLayout.addView(crown);
                crown.setX(look.getX());
                crown.setY(y);
            }
        });
    }
    public void takeCrownFrom(Actor oldLeader, final int cellsize) {
        this.crown= oldLeader.crown;
        oldLeader.crown=null;
        final Actor actor =this;
        this.crown.post(new Runnable() {
            @Override
            public void run() {
                crown.animate()
                        .x(actor.look.getX())
                        .y(actor.look.getY()-cellsize)
                        .setDuration(300);
            }
        });
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    private int speed =1000;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
//        if (!alive){
//            if(this instanceof Bot){
//                if (Bot.bots.size()==0){
//                    //win
//                }
//            }
//
//            if (this instanceof Player){
//                //lose
//            }
//        }
    }

    private boolean alive = true;

    public boolean isFrozen() {
        if (this instanceof Player&&this.getCostume()instanceof AntiFreeze)
            return false;
        return frozen;
    }

    private boolean frozen = false;

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible, final int duration, final Actor actor) {
        this.invincible = invincible;
        if (invincible){
            new Thread(new Runnable() {
                int d =duration;
                @Override
                public void run() {
                    while(d>0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        d -= 1000;
                    }
                    setInvincible(false,0, null);

                    if (actor instanceof Player)
                        actor.getLook().post(new Runnable() {
                            @Override
                            public void run() {
                                actor.setLook(actor.getCostume().getDrawableID());
                            }
                        });
                    else actor.getLook().post(new Runnable() {
                        @Override
                        public void run() {
                            actor.setLook(R.drawable.bot);
                        }
                    });
                }
            }).start();
        }
    }

    boolean invincible = false;

    public boolean isMining() {
        return mining;
    }

    public void setMining(final boolean mining, final int duration) {
        this.mining = mining;
        if (mining){
            new Thread(new Runnable() {
                int d =duration;
                @Override
                public void run() {
                    while(d>0){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        d-=1000;
                    }
                    setMining(false,0);

                }
            }).start();
        }
    }

    boolean mining = false;

    public void freeze(final int duration, final Cell cell){
        this.frozen = true;
        new Thread(new Runnable() {
            int d = duration;
            @Override
            public void run() {
                while(d>0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    d-=1000;
                }
                unFreeze();
                //cell.getPowerUp().setActive(false);
                //cell.cellView.setImageResource(R.drawable.explored_cell);
//                cell.setPowerUp(null);
            }
        }).start();
    }

    private void unFreeze(){
        this.frozen = false;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished() {
        this.finished = true;
    }

    public boolean finished = false;

    public void addBonus(int amount, Game game){
        this.bonus+=amount;
        if (this instanceof Player)
        game.bonusField.setText(Integer.toString(this.bonus));
    }

    public void addBonus(final Game game, Cell cell, boolean isPlayer) {
        this.bonus++;
        if (isPlayer)
            game.bonusField.setText(Integer.toString(bonus));
        final ImageView iv = new ImageView(game);
        iv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        iv.setImageResource(R.drawable.bonus_popup);
        game.mainLayout.addView(iv);
        iv.setX(cell.cellView.getX());
        iv.setY(cell.cellView.getWidth()*cell.pos.row);
        iv.animate()
                .translationYBy(-150f)
                .alpha(0)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        game.mainLayout.removeView(iv);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

    }

    public void teleport(Cell from, Cell to,Game game){
        //from.removeActor(this);
        //to.addActor(this);

        to.setExplored(this, game);
        this.pos.row= Table.ROWS-1;
        this.look.animate()
                .y(this.look.getWidth()*this.pos.row)
                .setDuration(200);
        if (this.crown!=null){
            crown.animate()
                    .y(this.look.getWidth()*this.pos.row)
                    .setDuration(200);
        }
    }

//    public void spring(Cell to, final Game game){
//        //from.removeActor(this);
//        //to.addActor(this);
//        Log.d("happyspring", "spring: ");
//
//        to.setExplored(this,game);
//        this.pos.row= to.pos.row;
//        this.pos.col = to.pos.col;
//        final Actor actor =this;
//        this.look.animate()
//                .x(to.cellView.getX())
//                .y(this.look.getWidth()*this.pos.row)
//                .setDuration(20000)
//                .setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animator) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animator) {
//                        if (!game.table.gameOver){
//                            actor.setCanmove(true);
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animator) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animator) {
//
//                    }
//                });
//
//        if (this.crown!=null){
//            crown.animate()
//                    .x(to.cellView.getX())
//                    .y(this.look.getWidth()*2*this.pos.row)
//                    .setDuration(2000);
//
//        }
//    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    protected int bonus=0;

    public int move(){
        return 0;
    }


}
