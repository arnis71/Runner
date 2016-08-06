package com.arnis.tt.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arnis.tt.ContinentsAdapter;
import com.arnis.tt.actors.Actor;
import com.arnis.tt.actors.Bot;
import com.arnis.tt.costumes.Costume;
import com.arnis.tt.base.Direction;
import com.arnis.tt.base.Mine;
import com.arnis.tt.OnGameOverListener;
import com.arnis.tt.powerUps.PowerUp;
import com.arnis.tt.R;
import com.arnis.tt.base.Table;

public class Game extends AppCompatActivity {

    public RelativeLayout mainLayout;
    public Table table;
    GestureDetector gd;
    public TextView bonusField;
    int width;
    boolean switcher =true;
    boolean autoMoving;
    private PopupWindow popupWin;
    private PopupWindow popupLoss;
    private ImageView activePowerup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainLayout = (RelativeLayout)findViewById(R.id.mainView);
        bonusField = (TextView)findViewById(R.id.bonus_field);
        activePowerup = (ImageView)findViewById(R.id.active_powerup);


        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        width = display.widthPixels;

        setUpPopUps();
        getAutoMoving();

        table = new Table(this,width,autoMoving);
        table.drawCells();

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                activePowerup.setImageResource(Costume.getActiveCostume().getDrawableID());
                table.addActors();
                Direction dir = new Direction(Table.getPlayer(),autoMoving);
                gd = new GestureDetector(getApplicationContext(), dir);

                mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        table.setOnGameOverListener(new OnGameOverListener() {
            @Override
            public void onGameOver(boolean win,int amount) {
                MyPassport.coins.assignCoins(amount);
                cleanUp();
                if (win){
                    popupWin.showAtLocation(mainLayout,Gravity.CENTER,0,0);
                }
                else {
                    popupLoss.showAtLocation(mainLayout,Gravity.CENTER,0,0);
                }
            }
        });


    }


    private void setUpPopUps() {
            View winPopup = LayoutInflater.from(this).inflate(R.layout.winner_popup,null);
            popupWin = new PopupWindow(winPopup, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            Button newGameWin = (Button) winPopup.findViewById(R.id.win_popup_new_game);
            Button menuWin = (Button) winPopup.findViewById(R.id.win_popup_menu);
            newGameWin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),Game.class);
                    startActivity(intent);
                }
            });
            menuWin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),Menu.class);
                    startActivity(intent);
                }
            });

            View lossPopup = LayoutInflater.from(this).inflate(R.layout.loser_popup,null);
            popupLoss = new PopupWindow(lossPopup, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            Button newGameLoss = (Button) lossPopup.findViewById(R.id.los_popup_new_game);
            Button menuLoss = (Button) lossPopup.findViewById(R.id.los_popup_menu);
            newGameLoss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),Game.class);
                    startActivity(intent);
                }
            });
            menuLoss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),Menu.class);
                    startActivity(intent);
                }
            });
        }



    private void getAutoMoving() {
//        Bundle bundle = getIntent().getExtras();
//        if (bundle!=null){
//            autoMoving = bundle.getBoolean(Settings.AUTOMOVING,false);
//        }
        SharedPreferences preferences = getSharedPreferences(MyPassport.DB,MODE_PRIVATE);
        autoMoving = preferences.getBoolean(MyPassport.AUTOMOVING,true);
    }

    @Override
    protected void onDestroy() {                    //DOES NOT PAUSE WHEN HOME BUTTON PRESSED
        cleanUp();
        super.onDestroy();
    }

    private void cleanUp(){
//        MP.mediaPlayer.release();
//        MP.mediaPlayer = null;
        if (table.cdt!=null)
            table.cdt.cancel();
        if (table.crownThread!=null&&!table.crownThread.isInterrupted())
            table.crownThread.interrupt();
        for (Bot b:Bot.bots) {
            b.setAlive(false);
            b=null;
        }
        Actor.crownExists=false;
        Bot.RUSHING_MODE=false;
        Bot.terminateThreads(false);
        Bot.bots.clear();
        Mine.mines.clear();
        PowerUp.pus.clear();
        if (Table.player.walking!=null&&!Table.player.walking.isInterrupted())
            Table.player.walking.interrupt();
        Table.player.setAlive(false);
        Table.player=null;
        if (table.cdt!=null){
            table.cdt.cancel();
        }
    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {

            gd.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        cleanUp();
        Intent intent = new Intent(this,Menu.class);
        startActivity(intent);

    }

    public void previewBombs(View view) {
//        cleanUp();
//        popupWin.showAtLocation(mainLayout,Gravity.CENTER,0,0);


//        table.useBonus(Table.BONUS_BOMBS);
        if (switcher){
            Mine.exposeAllMines(false);
            switcher=false;
        }else {
            Mine.hideAllMines();
            switcher=true;
        }
    }

    public void previewPowerups(View view) {
        //table.useBonus(Table.BONUS_POWERUPS);
        PowerUp.exposeAllPowerups(true);
//        cleanUp();
//        popupLoss.showAtLocation(mainLayout,Gravity.CENTER,0,0);
    }

    public static class MP{
        public static MediaPlayer mediaPlayer;

        public static MediaPlayer getMP(Context context, int id) {
                mediaPlayer = MediaPlayer.create(context,id);
            return mediaPlayer;
            }

    }


}
