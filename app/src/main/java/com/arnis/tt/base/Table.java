package com.arnis.tt.base;

import android.animation.Animator;
import android.os.CountDownTimer;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arnis.tt.activities.Game;
import com.arnis.tt.actors.Actor;
import com.arnis.tt.actors.Bot;
import com.arnis.tt.actors.Player;
import com.arnis.tt.costumes.BombSense;
import com.arnis.tt.costumes.Costume;
import com.arnis.tt.costumes.PowerUpSense;
import com.arnis.tt.OnGameOverListener;
import com.arnis.tt.powerUps.PowerUp;
import com.arnis.tt.powerUps.Spring;
import com.arnis.tt.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;

public class Table {
    public static final int BOT_COUNT = 9;
    public static final int COLUMNS = 11;
    public static final int ROWS = 14;
    public static final int BONUS_BOMBS =10;
    public static final int BONUS_POWERUPS =11;
    public static final int WINNER_WAIT = 10000;


    private TableLayout tableView;
    private GridLayout grid;
    private RelativeLayout mainView;
    public Game game;
    private TextView level;

    int waitTime;
    public static int LVL;
    TableRows tableRows =  new TableRows();
    private int cellSize;
    private Cell[][] cells;
    ArrayList<Actor> winners;
    ArrayList<Actor> dead;
    public static Player player;
    boolean firstFinished=true;
    public CountDownTimer cdt;
    public boolean autoMoving;
    private int killed;
    private Actor oldLeader=null;
    private Actor newLeader=null;
    private boolean firstGo =true;
    public Thread crownThread;
    public boolean gameOver;

    OnGameOverListener mOnGameOverListener;

    public void setOnGameOverListener(OnGameOverListener listener){
        mOnGameOverListener=listener;
    }


    public static Player getPlayer() {
        return player;
    }

    public Cell getFromCells(int i,int j){
        if (i<=-1||j<=-1||i>=ROWS||j>=COLUMNS)
            return null;
        return cells[i][j];
    }

    public Table(Game game, int width, boolean autoMoving) {
        LVL=1;
        tableView = (TableLayout) game.findViewById(R.id.table);
        grid = (GridLayout)game.findViewById(R.id.grid);
        mainView = (RelativeLayout) game.findViewById(R.id.mainView);
        level = (TextView) game.findViewById(R.id.level);
        this.game = game;
        this.cellSize =width/COLUMNS;
        cells = new Cell[ROWS][COLUMNS];
        winners = new ArrayList<>();
        dead = new ArrayList<>();
        this.autoMoving = autoMoving;
        Bot.botThreads = Executors.newFixedThreadPool(Table.BOT_COUNT);
        killed=0;
        gameOver=false;
    }

    public void setUpForNextLVL(){
        for (Actor a:dead){
            if (a instanceof Bot){
                Bot.bots.remove(a);
            }
        }
        winners = new ArrayList<>();
        dead = new ArrayList<>();
        LVL++;
        level.setText("Level " +Integer.toString(LVL));
        firstFinished = true;
        Bot.RUSHING_MODE=false;
        if (LVL==2){
            crownThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.interrupted()&&player!=null&&!gameOver){
                        try {
                            if (!gameOver)
                                checkLeader();
                            Thread.sleep(1000);
                            Log.d("happycrown", "run: crownThread");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            crownThread.start();
        }

    }

    public void drawCells(){

        tableRows.clearRows();

        for (int i = 0; i < ROWS; i++) {
            tableRows.newRow();
            for (int j = 0; j < COLUMNS; j++) {
                ImageView iv = new ImageView(game);
                iv.setLayoutParams(new TableRow.LayoutParams(cellSize,cellSize));
                //iv.setPadding(10,10,10,10);
//                iv.setMaxHeight(cellSize);
//                iv.setMaxWidth(cellSize);
                iv.setImageResource(R.drawable.export55);
                tableRows.getCurrent().addView(iv);
                cells[i][j] = new Cell(iv,new Position(i,j));
                if (i<ROWS-2) {
                    cells[i][j].powerUp=PowerUp.setPowerUp(cells[i][j], getChanceOfPowerUp());
                    if (cells[i][j].getPowerUp()==null)
                        cells[i][j].mine = Mine.setMine(getChanceOfMine(), cells[i][j]);
                }
                if (cells[i][j].getExploredDrawable()==null)
                    cells[i][j].setExploredDrawable(R.drawable.explored_cell);
            }
            tableView.addView(tableRows.getCurrent());
        }

        if (Costume.getActiveCostume()instanceof BombSense)
            drawBombWarnings();

        if (Costume.getActiveCostume()instanceof PowerUpSense)
            drawPowerupWarnings();

    }

    private void drawPowerupWarnings() {
        ArrayList<Cell> warnings = new ArrayList<>();
        for (Pair pair:PowerUp.pus){
            Cell center = (Cell) pair.second;
            Cell side;

            side = getFromCells(center.pos.row-1,center.pos.col);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row-1,center.pos.col-1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row,center.pos.col-1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row+1,center.pos.col-1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row+1,center.pos.col);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row+1,center.pos.col+1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row,center.pos.col+1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row-1,center.pos.col+1);
            if (side!=null)
                warnings.add(side);
        }

        for (Cell c:warnings){
            c.setExploredDrawable(R.drawable.near_powerup);
        }
    }

    private void drawBombWarnings() {
        ArrayList<Cell> warnings = new ArrayList<>();
        for (Pair pair:Mine.mines){
            Cell center = (Cell) pair.second;
            Cell side;

            side = getFromCells(center.pos.row-1,center.pos.col);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row-1,center.pos.col-1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row,center.pos.col-1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row+1,center.pos.col-1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row+1,center.pos.col);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row+1,center.pos.col+1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row,center.pos.col+1);
            if (side!=null)
                warnings.add(side);
            side = getFromCells(center.pos.row-1,center.pos.col+1);
            if (side!=null)
                warnings.add(side);
        }

        for (Cell c:warnings){
            c.setExploredDrawable(R.drawable.exclamation);
        }
    }

    private int getChanceOfMine(){
       Random rnd = new Random();
        //return rnd.nextInt(30-LVL);
        if (LVL<50){
           return rnd.nextInt(55-LVL);
        }
//        if (10<LVL&&LVL<=20){
//            return rnd.nextInt();
//        }
//        if (20<LVL&&LVL<=30){
//            return rnd.nextInt(3)+29;
//        }
//        if (30<LVL&&LVL<=40){
//            return rnd.nextInt(3)+39;
//        }
//        if (40<LVL&&LVL<=50){
//            return rnd.nextInt(3)+49;
//        }
        if (LVL>50){
            return rnd.nextInt(5);
        }

        return 0;

    }

    private int getChanceOfPowerUp(){//change values
        Random rnd = new Random();
        if (LVL<50)
            return rnd.nextInt(70-LVL);
        //return rnd.nextInt(80-LVL);
//        if (LVL<10){
//            return rnd.nextInt(3)+80;
//        }
//        if (10<LVL&&LVL<=20){
//            return rnd.nextInt(3)+70;
//        }
//        if (20<LVL&&LVL<=30){
//            return rnd.nextInt(3)+60;
//        }
//        if (30<LVL&&LVL<=40){
//            return rnd.nextInt(3)+50;
//        }
//        if (40<LVL&&LVL<=50){
//            return rnd.nextInt(3)+40;
//        }
        if (LVL>50){
            return rnd.nextInt(20);
        }

        return 0;
    }

    private Cell getStartCell(){
        return cells[13][5];
    }


    public void addActors(){
        boolean doOnce= true;
        for (int i = 0; i < BOT_COUNT+1; i++) {
            ImageView iv = new ImageView(game);
            iv.setLayoutParams(new TableRow.LayoutParams(cellSize,cellSize));
            if (doOnce){
                //look = LayoutInflater.from(game).inflate(R.layout.player_layout,null);
                //iv.setImageResource(R.drawable.player_icon);
                doOnce=false;
                player = new Player(iv,new Position(13,5),this);
                Costume.dressPlayer(player);
                //getStartCell().addActor(player);
                getStartCell().setExplored(player, game);
                player.startWalking(autoMoving);
            }else {
                iv.setImageResource(R.drawable.bot);
                final Bot bot = new Bot(iv,new Position(13,5),this);
                bot.setSmartLvl();
                bot.initiateBot();
            }
            mainView.addView(iv);
            iv.setX(getStartCell().cellView.getX());
            iv.setY((getStartCell().pos.row)*cellSize);

        }
    }

    private void transitionTable(){
        tableView.animate()
                .yBy(tableView.getHeight())
                .setDuration(200)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!gameOver) {
                            tableView.setY(0);
                            removeDead();
                            initiateWinners();
                            setUpForNextLVL();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    public void teleportWinners(){
//        Animator.AnimatorListener animator = new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                Log.d("happy", "onAnimationEnd: ");
//                initiateWinners();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        };
        for (Actor a:winners)
            a.teleport(cells[a.pos.row][a.pos.col],cells[ROWS-1][a.pos.col], game);
    }

    public void initiateWinners(){
        Bot.terminateThreads(true);
        for (Actor a:winners){
            a.finished=false;
            if (a instanceof Bot){
                ((Bot) a).initiateBot();
            } else ((Player)a).startWalking(autoMoving);
        }
    }

    public void moveActor(Actor actor,Cell to){
        Log.d("happyanimate", "moveActor: ");
        if (moveData(actor,cells[actor.pos.row][actor.pos.col],to)){
            actor.pos.row=to.pos.row;
            actor.pos.col=to.pos.col;
            animateView(actor,to.cellView.getX(),tableRows.rows.get(actor.pos.row).getY(),200);
        }

    }

    public void moveActor(Actor actor,int direction){
       // Log.d("happy", "moveActor: "+Integer.toString(direction));
        if (autoMoving&&actor instanceof Player&&((actor.pos.col == 0 && direction == Direction.MOVE_LEFT) || (actor.pos.col == 10 && direction == Direction.MOVE_RIGHT)
                    || (actor.pos.row == 0 && direction == Direction.MOVE_UP) || (actor.pos.row == 13 && direction == Direction.MOVE_DOWN)))
            return;

//        if (actor instanceof Player&&actor.getCostume()instanceof BombSense){
//            boolean b = checkSurroindings(actor);
//            Log.d("happysurround", "checkSurroindings: "+b);
//            if (b)
//                warning(getFromCells(actor.pos.row,actor.pos.col));
//            else hideWarning();
//        }

        switch (direction) {
            case 0: return;
            case 1:if (moveData(actor,cells[actor.pos.row][actor.pos.col],cells[actor.pos.row-1][actor.pos.col])){
                    animateView(actor,-1,tableRows.rows.get(actor.pos.row-1).getY(),200);
                    //actor.look.animate().y(tableRows.rows.get(actor.pos.row-1).getY()).setDuration(200);
                    actor.pos.row--;
                }break;
            case 2:if (moveData(actor,cells[actor.pos.row][actor.pos.col],cells[actor.pos.row][actor.pos.col-1])){
                animateView(actor,cells[actor.pos.row][actor.pos.col-1].cellView.getX(),-1,200);
                //actor.look.animate().x(cells[actor.pos.row][actor.pos.col-1].cellView.getX()).setDuration(200);
                actor.pos.col--;
                }break;
            case 3:if (moveData(actor,cells[actor.pos.row][actor.pos.col],cells[actor.pos.row][actor.pos.col+1])){
                animateView(actor,cells[actor.pos.row][actor.pos.col+1].cellView.getX(),-1,200);
                //actor.look.animate().x(cells[actor.pos.row][actor.pos.col+1].cellView.getX()).setDuration(200);
                actor.pos.col++;
                }break;
            case 4:if (moveData(actor,cells[actor.pos.row][actor.pos.col],cells[actor.pos.row+1][actor.pos.col])){
                animateView(actor,-1,tableRows.rows.get(actor.pos.row+1).getY(),200);
                //actor.look.animate().y(tableRows.rows.get(actor.pos.row+1).getY()).setDuration(200);
                actor.pos.row++;
                }break;
        }
        checkIfWinner(actor);
    }

//    private void hideWarning() {
//        if (player.exclamation!=null)
//            player.exclamation.setVisibility(View.INVISIBLE);
//    }
//
//    private void warning(Cell at) {
//        if (player.exclamation!=null){
//            player.exclamation.setX(at.cellView.getX());
//            player.exclamation.setY(tableRows.rows.get(at.pos.row).getY());
//            player.exclamation.setVisibility(View.VISIBLE);
//
//        }else {
//            player.exclamation = new ImageView(game);
//            player.exclamation.setLayoutParams(new RelativeLayout.LayoutParams(cellSize, cellSize));
//            player.exclamation.setImageResource(R.drawable.exclamation);
//            player.exclamation.setX(at.cellView.getX());
//            player.exclamation.setY(tableRows.rows.get(at.pos.row).getY());
//            game.mainLayout.addView(player.exclamation);
//        }
//    }

    private boolean checkSurroindings(Actor actor){
        if (!Mine.lookFor(getFromCells(actor.pos.row-1,actor.pos.col))||
                !Mine.lookFor(getFromCells(actor.pos.row-1,actor.pos.col-1))||
                !Mine.lookFor(getFromCells(actor.pos.row,actor.pos.col-1))||
                !Mine.lookFor(getFromCells(actor.pos.row+1,actor.pos.col-1))||
                !Mine.lookFor(getFromCells(actor.pos.row+1,actor.pos.col))||
                !Mine.lookFor(getFromCells(actor.pos.row+1,actor.pos.col+1))||
                !Mine.lookFor(getFromCells(actor.pos.row,actor.pos.col+1))||
                !Mine.lookFor(getFromCells(actor.pos.row-1,actor.pos.col+1)))
            return true;
        else return false;
    }

    private void checkLeader() {
        if (!gameOver){
            int bonus=0;
            for (Bot b:Bot.bots){
                if (b.isAlive()&&b.getBonus()>bonus){
                    newLeader = b;
                    bonus = b.getBonus();
                }
            }
            if (player.isAlive()&&player.getBonus()>bonus){
                newLeader =player;
            }
            if (!firstGo&&newLeader!=oldLeader){

                newLeader.takeCrownFrom(oldLeader,cellSize);

                Log.d("happycrown", "take crown");
            }
            if (firstGo){
                Log.d("happycrown", "addcrown");

                newLeader.addCrown(game,newLeader.look.getY()-cellSize,cellSize);

                firstGo=false;
            }

            oldLeader = newLeader;
        }
    }

    private void animateView(Actor actor,float x,float y,int duration){
        if (actor instanceof Player)
            Log.d("happyanimate", "animateView: ");
        if (actor.crown==null){
            if (x==-1)
                actor.look.animate().y(y).setDuration(duration);
            else if (y==-1)
                actor.look.animate().x(x).setDuration(duration);
            else actor.look.animate().x(x).y(y).setDuration(duration);
        }
        else {
            if (x==-1){
                actor.look.animate().y(y).setDuration(duration);
                actor.crown.animate().y(y-cellSize).setDuration(duration);
            }
            else if (y==-1){
                actor.look.animate().x(x).setDuration(duration);
                actor.crown.animate().x(x).setDuration(duration);
            } else {
                actor.look.animate().x(x).y(y).setDuration(duration);
                actor.crown.animate().x(x).y(y-cellSize).setDuration(duration);
            }
        }
    }

    private boolean moveData(Actor actor,Cell from,Cell to){
//Game.MP.getMP(game.context,R.raw.footstep).start();

        if (to.mine==null) {
            if (!to.isExplored()&&to.getPowerUp()==null){
                boolean isPlayer = false;
                if (actor instanceof Player){
                    isPlayer=true;
                }
                actor.addBonus(game,to,isPlayer);
                //Log.d("happy", "explored cell");
            }
            //from.removeActor(actor);
            if (actor.isMining()){
                Mine.setMine(from);
            }
            if (to.getPowerUp()!=null&&!to.getPowerUp().isActive()) {
                return true;
            }

//            if (from.getPowerUp()!=null&&from.getPowerUp().delayable)
//                from.activateDelayed(actor,game);

            to.setExplored(actor, game);

            if (to.getPowerUp()!=null&to.getPowerUp() instanceof Spring)
                return false;
            return true;
        } else {
            if (actor.isInvincible()){
                to.mine.visible=true;
                to.mine.active=false;
                return true;
            }
            Game.MP.getMP(game,R.raw.boom).start();
            to.mine.makeVisible(to,false);
            killActor(actor);
            actor.setAlive(false);
            return false;
        }
    }

    private void checkIfWinner(Actor actor){
        if (actor.pos.row==0){
            actor.setFinished();
            winners.add(actor);
            if (firstFinished&&!gameOver) {
                Log.d("happycdt", "cdt start ");
                Bot.rushToTop();
                actor.addBonus(5, game);
                firstFinished=false;
                Game.MP.getMP(game,R.raw.countdown).start();
                if (LVL<50)
                    waitTime = WINNER_WAIT-((LVL-1)*180);
                Toast.makeText(game,"First actor finished... "+ waitTime/1000 +" seconds until next level",Toast.LENGTH_LONG).show();
                cdt = new CountDownTimer(waitTime, 1000) {
                    @Override
                    public void onTick(long l) {
                        Log.d("happy", "onTick: ");
                    }

                    @Override
                    public void onFinish() {
                        if (!gameOver) {
                            Game.MP.getMP(game, R.raw.transition).start();
                            killLoosers();
                            drawCells();
                            transitionTable();
                            teleportWinners();
                        }
                    }
                }.start();
            }
            PowerUp.exposeAllPowerups(false);
            Mine.exposeAllMines(true);
        }
    }

    private void killLoosers() {
        if (!player.isFinished()&&player.isAlive())
            killActor(player);

        for (Bot b:Bot.bots){
            if (!b.isFinished()&&b.isAlive())
                killActor(b);
        }
    }

    private void killActor(Actor actor){
        actor.setAlive(false);
        actor.look.setImageResource(R.drawable.dead);
        dead.add(actor);
        killed++;
        Log.d("happykilled", "killed " + Integer.toString(killed)+ " of "+ Integer.toString(BOT_COUNT+1));
        if (killed==BOT_COUNT&&player.isAlive()) {
            PowerUp.exposeAllPowerups(false);
            Mine.exposeAllMines(true);
            gameOver=true;
            if (cdt!=null)
                cdt.cancel();
            if (crownThread!=null&&!crownThread.isInterrupted())
                crownThread.interrupt();
            mOnGameOverListener.onGameOver(true,player.getBonus());
        }
        if (actor instanceof Player){
            PowerUp.exposeAllPowerups(false);
            Mine.exposeAllMines(true);
            gameOver=true;
            if (cdt!=null)
                cdt.cancel();
            if (crownThread!=null&&!crownThread.isInterrupted())
                crownThread.interrupt();
            mOnGameOverListener.onGameOver(false,player.getBonus());
        }
    }

    private void removeDead(){
        for (Actor a:dead){
            game.mainLayout.removeView(a.look);
            a=null;
        }
    }

    public void useBonus(int bonus) {

        switch (bonus){
            case BONUS_BOMBS:
                if (player.getBonus()>=20){
                    Game.MP.getMP(game,R.raw.cash).start();
                    player.useBonus(20);
                    game.bonusField.setText(Integer.toString(player.getBonus()));
                    Mine.exposeAllMines(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            game.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Mine.hideAllMines();
                                }
                            });
                        }
                    }).start();
                }break;

            case BONUS_POWERUPS:
                if (player.getBonus()>=10){
                    Game.MP.getMP(game,R.raw.cash).start();
                    player.useBonus(10);
                    game.bonusField.setText(Integer.toString(player.getBonus()));
                    PowerUp.exposeAllPowerups(true);
                }break;

        }


    }


    class TableRows{
        public ArrayList<TableRow> rows;

        public TableRows() {
            this.rows = new ArrayList<>();
        }

        public void newRow(){
            TableRow tableRow = new TableRow(game);
            TableRow.LayoutParams t = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            t.setMargins(100,0,0,0);
            tableRow.setLayoutParams(t);
            rows.add(tableRow);
        }

        public TableRow getCurrent(){
            return rows.get(rows.size()-1);
        }

        public void clearRows(){
            if (rows.size()!=0) {
                for (TableRow row : rows) {
                    tableView.removeView(row);
                    row = null;
                }
                rows.clear();
            }
        }
    }
}
