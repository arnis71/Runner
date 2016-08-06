package com.arnis.tt.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arnis.tt.LevelSelectionHelper;
import com.arnis.tt.R;
import com.arnis.tt.base.Direction;
import com.arnis.tt.base.Table;
import com.arnis.tt.costumes.Costume;

import java.util.ArrayList;
import java.util.Map;

public class LevelSelection extends AppCompatActivity implements View.OnTouchListener {

    private float dY;
    private TextView bar;
    RelativeLayout rootLevel;
    public static final String CIRCLES_DB = "cricles";
    int height;
    LevelSelectionHelper helper;
    private RelativeLayout mainContent;
    RelativeLayout footer;
    ImageView lastOutline;
    private ImageView playerIcon;
    boolean[] circlesAvaliability;
    Integer lasti;
    private TextView upBar;
    private boolean showOnce=true;
    private LinearLayout column;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        rootLevel = (RelativeLayout) findViewById(R.id.root_level);
        mainContent  = (RelativeLayout)findViewById(R.id.main_content);
        bar = (TextView)findViewById(R.id.bar);
        playerIcon = (ImageView)findViewById(R.id.player_at);
        upBar = (TextView)findViewById(R.id.upBar);
        column = (LinearLayout)findViewById(R.id.circle_column);

        helper = new LevelSelectionHelper(this);
        helper.handleIncomingIntent(getIntent());
        helper.setCurrentCountry(bar);


        populateCircles();

        rootLevel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                helper.toggleFooter(footer);
                helper.toggleUpBar(upBar);
                setUpCircles();

                rootLevel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        height = display.heightPixels;

        rootLevel.setOnTouchListener(this);

    }


    private void populateCircles() {
        circlesAvaliability = new boolean[5];
        SharedPreferences prefs = getSharedPreferences(CIRCLES_DB,MODE_PRIVATE);
//        prefs.edit().clear().apply();
        boolean def=true;
        for (int i = 0; i < 5; i++) {
            if (i==1)
                def=false;
            circlesAvaliability[i] = prefs.getBoolean(helper.getCurrentCountry()+Integer.toString(i),def);
        }
    }
    private void setUpCircles() {
        setUpCircle(R.id.cricle_1,0);
        setUpCircle(R.id.cricle_2,1);
        setUpCircle(R.id.cricle_3,2);
        setUpCircle(R.id.cricle_4,3);
        setUpCircle(R.id.cricle_5,4);



    }
    private void setUpCircle(int id, final int i){
//        View circle = findViewById(id);
//        final ImageView outline = (ImageView)circle.findViewById(R.id.outline);
        final ImageView icon = (ImageView)/*circle.*/findViewById(/*R.id.icon*/id);
        final ImageView reveal = (ImageView) findViewById(R.id.circular_reveal);
        icon.setImageResource(R.drawable.eiffel);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (circlesAvaliability[i]){//single tap
                    if (lasti!=null&&lasti==i){//double tap
                        shrinkAllChild(icon);
                        reveal.animate().scaleXBy(15).scaleYBy(15).setDuration(700).setInterpolator(new AccelerateInterpolator())
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(),Game.class);
                                        startActivity(intent);
                                    }
                                })
                                .start();
                        return;
                    }
                    reveal.setVisibility(View.VISIBLE);
                    reveal.setX(column.getX()-((reveal.getWidth()-icon.getWidth())/2));
                    AnimatorSet set = new AnimatorSet();
                    ObjectAnimator scaleXout = ObjectAnimator.ofFloat(reveal, View.SCALE_X,0);
                    ObjectAnimator scaleYout = ObjectAnimator.ofFloat(reveal, View.SCALE_Y,0);
                    ObjectAnimator scaleXin = ObjectAnimator.ofFloat(reveal, View.SCALE_X,1);
                    ObjectAnimator scaleYin = ObjectAnimator.ofFloat(reveal, View.SCALE_Y,1);
                    ObjectAnimator transYout = ObjectAnimator.ofFloat(reveal, View.TRANSLATION_Y,icon.getY()+mainContent.getY()-((reveal.getWidth()-icon.getWidth())/2));
                    scaleXout.setDuration(300).setInterpolator(new AccelerateInterpolator());
                    scaleYout.setDuration(300).setInterpolator(new AccelerateInterpolator());
                    scaleXin.setDuration(300).setInterpolator(new BounceInterpolator());
                    scaleYin.setDuration(300).setInterpolator(new BounceInterpolator());
                    transYout.setDuration(0);
                    set.play(scaleXout).with(scaleYout);
                    set.play(transYout).after(scaleYout);
                    set.play(scaleXin).with(scaleYin).after(transYout);
                    set.start();

//                    reveal.animate().scaleYBy(0).scaleXBy(0)
////                            .x(column.getX()-((reveal.getWidth()-icon.getWidth())/2))
////                            .y(icon.getY()+mainContent.getY()-((reveal.getWidth()-icon.getWidth())/2))
//                            .setDuration(500)
//                            .setInterpolator(new BounceInterpolator()).start();
//                            .withEndAction(new Runnable() {
//                                @Override
//                                public void run() {
//                                    reveal.animate().scaleX(1f).scaleY(1f).setDuration(500).setInterpolator(new BounceInterpolator()).start();
//                                }
//                            })
//                            .start();
//                    reveal.setX(column.getX()-((reveal.getWidth()-icon.getWidth())/2));
//                    reveal.setY(icon.getY()+mainContent.getY()-((reveal.getWidth()-icon.getWidth())/2));

//                    animateOutline(outline);
//                    int[] coo = new int[2];
//                    icon.getLocationInWindow(coo);
                    playerIcon.animate().y(icon.getY()).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    lasti=i;
                }

            }
        });
        icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                circlesAvaliability[i]=true;
                return true;
            }
        });
    }

    private void shrinkAllChild(ImageView hash) {
        //hash.animate().scaleXBy(5f).scaleYBy(5f).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        shrinkView(playerIcon);
        for (int i = 0; i < column.getChildCount(); i++) {
            View v =column.getChildAt(i);
            if (v!=hash)
                shrinkView(v);
        }
    }
    private void shrinkView(View view) {
        view.animate().scaleY(0).scaleX(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
    }
//    private void animateOutline(ImageView outline) {
//        if (lastOutline!=null)
//            lastOutline.animate().scaleXBy(-0.2f).scaleYBy(-0.2f).setInterpolator(new BounceInterpolator()).setDuration(500).start();;
//        outline.animate().scaleXBy(0.2f).scaleYBy(0.2f).setInterpolator(new BounceInterpolator()).setDuration(500).start();
//        lastOutline=outline;
//    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (showOnce&&motionEvent.getY()>height/3){
            upBar.animate().y(0).setDuration(500).setInterpolator(new BounceInterpolator()).start();
            showOnce=false;
        }

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                dY = bar.getY() - motionEvent.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("happy", "onTouch: " +Float.toString(motionEvent.getRawY()+dY));
                mainContent.animate().alpha(1f-(motionEvent.getRawY()+dY)/1000f)
                        .scaleX(1f-(motionEvent.getRawY()+dY)/1000f).scaleY(1f-(motionEvent.getRawY()+dY)/1000f)
                        .setDuration(0).start();
                bar.animate()
                        .y(motionEvent.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            case MotionEvent.ACTION_UP:
                if (bar.getY()<height/2){ //animate back not enough scroll
                    showOnce=true;
                    mainContent.animate().alpha(1f)
                            .scaleX(1f).scaleY(1f)
                            .setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    upBar.animate().y(-upBar.getHeight()).setDuration(500).setInterpolator(new BounceInterpolator()).start();
                    bar.animate()
                            .y(0)
                            .setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                } else if (helper.nextCountryAvaliable()) {//animate to next country
                    upBar.animate().y(height/2).setDuration(1000).setInterpolator(new BounceInterpolator()).start();
                    bar.animate()
                            .y(height-bar.getHeight())
                            .alpha(0)
                            .setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    saveCircles();
                                    helper.nextCountry();
                                    Intent intent = new Intent(getApplicationContext(),LoadingClouds.class);
                                    startActivity(intent);
                                }
                            })
                            .start();
                } else { //animate back next country not avaliable
                    Toast.makeText(this,"Next country not avaliable",Toast.LENGTH_SHORT).show();
                    mainContent.animate().alpha(1f).scaleX(1f).scaleY(1f)
                            .setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    upBar.animate().y(-upBar.getHeight()).setDuration(500).setInterpolator(new BounceInterpolator()).start();
                    bar.animate()
                            .y(0)
                            .setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                }
            default:
                return false;
        }
        return true;
    }

    private void saveCircles(){
        SharedPreferences prefs = getSharedPreferences(CIRCLES_DB,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < 5; i++) {
            editor.putBoolean(helper.getCurrentCountry()+Integer.toString(i),circlesAvaliability[i]).apply();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,ContinentSelection.class);
        startActivity(intent);
    }
}
