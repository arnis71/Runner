package com.arnis.tt.base;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.arnis.tt.actors.Player;


public class Direction extends GestureDetector.SimpleOnGestureListener {

    public static final int MOVE_UP=1;
    public static final int MOVE_LEFT=2;
    public static final int MOVE_RIGHT=3;
    public static final int MOVE_DOWN=4;

    Player player;

    long time1=0;
    long time2=5000;
    boolean autoMoving;

    public Direction(Player player, boolean autoMoving) {
        this.player = player;
        this.autoMoving =autoMoving;
    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//       switch(getSlope(0,0,e.getX(),e.getY())){
//           case 1:
//               Log.d("happy", "top");
//               if (actor.pos.row!=0)
//                   table.moveActor(actor,MOVE_UP);
//               return true;
//           case 2:
//               Log.d("happy", "left");
//               if (actor.pos.col!=0)
//                   table.moveActor(actor,MOVE_LEFT);
//               return true;
//           case 3:
//               Log.d("happy", "right");
//               if (actor.pos.col!=10)
//                   table.moveActor(actor,MOVE_RIGHT);
//               return true;
//           case 4:
//               Log.d("happy", "down");
//               if (actor.pos.row!=13)
//                   table.moveActor(actor,MOVE_DOWN);
//               return true;
//       }
//
//        return false;
//    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {

        time2 = System.currentTimeMillis();
        Log.d("happytime",Long.toString(time2-time1));
        if (time2-time1>=player.getSpeed()||autoMoving){
            time1 = time2;
            if (player.isAlive()&&!player.finished&&!player.isFrozen()){
                switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                    case 1:
                        Log.d("happy", "top");
                        if (player.pos.row!=0)
                            player.setDirection(MOVE_UP);
                        return true;
                    case 2:
                        Log.d("happy", "left");
                        if (player.pos.col!=0)
                            player.setDirection(MOVE_LEFT);
                        return true;
                    case 3:
                        Log.d("happy", "right");
                        if (player.pos.col!=10)
                            player.setDirection(MOVE_RIGHT);
                        return true;
                    case 4:
                        Log.d("happy", "down");
                        if (player.pos.row!=13)
                            player.setDirection(MOVE_DOWN);
                        return true;
                }
            }
        }


        return false;
    }

    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle>= -135)
            // down
            return 4;
        if (angle > -45 && angle <= 45){
            //Log.d("happy", "getSlope: " + angle);
            // right
            return 3;}
        return 0;
    }
}
