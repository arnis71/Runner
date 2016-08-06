//package com.arnis.runner.PowerUps;
//
//import com.arnis.runner.Actors.Actor;
//import com.arnis.runner.Base.Cell;
//import com.arnis.runner.Activities.Game;
//import com.arnis.runner.Base.Mine;
//import com.arnis.runner.R;
//
///**
// * Created by arnis on 07.07.2016.
// */
//public class EyeOfSauron extends PowerUp {
//    public EyeOfSauron() {
//        this.duration = 5000;
//        setActive(true);
//    }
//
//    @Override
//    public void activate(Cell cell, Actor actor, final Game game) {
//
//        if (actor.crown!=null){
//            duration+= 1000;
//        }
//        cell.cellView.setImageResource(R.drawable.eye_of_sauron);
//        this.setVisible(true);
//        cell.getPowerUp().setActive(false);
//        //Game.MP.getMP(game,R.raw.freeze).start();
//        Mine.exposeAllMines(false);
//        PowerUp.exposeAllPowerups(false);
//
//        new Thread(new Runnable() {
//            int d =duration;
//            @Override
//            public void run() {
//                while(d>0){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    d-=1000;
//                }
//                game.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Mine.hideAllMines();
//                        PowerUp.hideAllPowerups();
//                    }
//                });
//            }
//        }).start();
//
//    }
//}
