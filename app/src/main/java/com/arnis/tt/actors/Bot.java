package com.arnis.tt.actors;

import android.util.Log;
import android.widget.ImageView;

import com.arnis.tt.base.Cell;
import com.arnis.tt.base.Direction;
import com.arnis.tt.base.Mine;
import com.arnis.tt.base.Position;
import com.arnis.tt.base.Table;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by arnis on 01.07.2016.
 */
public class Bot extends Actor {
    public static ArrayList<Bot> bots = new ArrayList<>();
    public static ExecutorService botThreads;// = Executors.newFixedThreadPool(Table.BOT_COUNT);

    Table table;

    public static final int PRIMITIVE = 1;
    public static final int EXPLORER = 2;
    public static final int SAFETY_STEVE = 3;
    public static boolean RUSHING_MODE = false;
    public int smartLvl=0;

    public Bot(ImageView look, Position pos, Table table) {
        this.look = look;
        this.pos = pos;
        bots.add(this);
        this.table = table;
    }

    public void setSmartLvl() {
        Random rnd = new Random();
        this.smartLvl = rnd.nextInt(2)+1;
    }

    @Override
    public int move() {


        switch (smartLvl){
            case PRIMITIVE: return goPrimitive();
            case EXPLORER: return goExplorer();
            case SAFETY_STEVE: return goSafetySteve();
        }

        return 0;
    }

    public static void rushToTop(){
        RUSHING_MODE=true;
    }

    private int goSafetySteve() {
        Integer in = Cell.randmomizer(Bot.SAFETY_STEVE,checkPath());
        if (in!=null)
            return in;
        else return goPrimitive();
    }

    public int goPrimitive() {
        if (RUSHING_MODE&&checkVisibleMine(Direction.MOVE_UP))
            return 1;

        do {
            Random rnd = new Random();
            int go = rnd.nextInt(3)+1;
            if (go!=1)
                go = rnd.nextInt(3)+1;
            if (checkBounds(go)&&checkVisibleMine(go))
                return go;
        } while (this!=null);
        return 0;
    }


    private int goExplorer() {
        if (RUSHING_MODE&&checkVisibleMine(Direction.MOVE_UP))
            return 1;

        Integer in = Cell.randmomizer(Bot.EXPLORER,checkPath());
        if (in!=null)
            return in;
        else return goPrimitive();
    }

//    private int switcher(int go){
//        switch (go){
//            case Direction.MOVE_UP:
//                if (checkBounds(1)&&!checkExplored(1)&&checkVisibleMine(1))
//                    return 1;
//                break;
//            case Direction.MOVE_LEFT:
//                if (checkBounds(2)&&!checkExplored(2)&&checkVisibleMine(2))
//                    return 2;
//                break;
//            case Direction.MOVE_RIGHT:
//                if (checkBounds(3)&&!checkExplored(3)&&checkVisibleMine(3))
//                    return 3;
//                break;
//        }
//        return go;
//    }

    private boolean checkBounds(int direction){
        return !((this.pos.col == 0 && direction == Direction.MOVE_LEFT) || (this.pos.col == 10 && direction == Direction.MOVE_RIGHT)
                || (this.pos.row == 0 && direction == Direction.MOVE_UP) || (this.pos.row == 13 && direction == Direction.MOVE_DOWN));
    }

    private boolean checkVisibleMine(int direction){ //goes out of bounds
        switch (direction){
            case Direction.MOVE_UP: return Mine.lookFor(this,table.getFromCells(this.pos.row - 1, this.pos.col).mine);
            case Direction.MOVE_LEFT: return Mine.lookFor(this,table.getFromCells(this.pos.row, this.pos.col-1).mine);
            case Direction.MOVE_RIGHT: return Mine.lookFor(this,table.getFromCells(this.pos.row, this.pos.col+1).mine);
            case Direction.MOVE_DOWN: return Mine.lookFor(this,table.getFromCells(this.pos.row + 1, this.pos.col).mine);
            default: return true;
        }

    }
//    private boolean checkExplored(int direction){
//        switch (direction){
//            case Direction.MOVE_UP: return Cell.askIfExplored(table.getFromCells(this.pos.row - 1, this.pos.col));
//            case Direction.MOVE_LEFT: return Cell.askIfExplored(table.getFromCells(this.pos.row, this.pos.col-1));
//            case Direction.MOVE_RIGHT: return Cell.askIfExplored(table.getFromCells(this.pos.row, this.pos.col+1));
//            case Direction.MOVE_DOWN: return Cell.askIfExplored(table.getFromCells(this.pos.row + 1, this.pos.col));
//            default: return true;
//        }
//    }
    private Cell[] checkPath(){
        Cell[] cells = new Cell[4];
        int sch=0;
        if (checkBounds(1)&&Mine.lookFor(this,table.getFromCells(this.pos.row - 1, this.pos.col).mine))
            cells[sch]=table.getFromCells(this.pos.row - 1, this.pos.col);

        sch++;

        if (checkBounds(2)&&Mine.lookFor(this,table.getFromCells(this.pos.row, this.pos.col-1).mine))
            cells[sch]=table.getFromCells(this.pos.row, this.pos.col-1);

        sch++;

        if (checkBounds(3)&&Mine.lookFor(this,table.getFromCells(this.pos.row, this.pos.col+1).mine))
            cells[sch]=table.getFromCells(this.pos.row, this.pos.col+1);

        sch++;

        if (checkBounds(4)&&Mine.lookFor(this,table.getFromCells(this.pos.row+1, this.pos.col).mine))
            cells[sch]=table.getFromCells(this.pos.row+1, this.pos.col);


        return cells;
    }
    public void initiateBot(){
        final Bot bot = this;
        botThreads.execute(new Runnable() {
            @Override
            public void run() {
                while (bot.isAlive() && !bot.finished&& !botThreads.isShutdown()) {
                        try {
                            Thread.sleep(bot.getSpeed());

                            Log.d("happy", "run: " + Thread.currentThread().getName());
                            if (!bot.isFrozen()&&bot.isAlive() && !bot.finished) {
                                bot.look.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        table.moveActor(bot, bot.move());
                                        //Log.d("happy", "run: ");
                                    }
                                });
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }




                }
            }
        });
    }

    public static void terminateThreads(boolean setupNew){ //   works after next round
        if (botThreads!=null)
            botThreads.shutdownNow();
        botThreads = null;
        if (setupNew){
            Log.d("happyterminate", "terminateThreads: "+Integer.toString(bots.size()));
            botThreads = Executors.newFixedThreadPool(bots.size());
        }
    }

}
