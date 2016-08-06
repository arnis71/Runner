package com.arnis.tt.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.arnis.tt.R;

import java.util.Random;

public class LoadingClouds extends AppCompatActivity {

   boolean spawn = true;
    int height;
    int width;
    Random rnd;
    RelativeLayout sky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_clouds);

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        height = display.heightPixels;
        width = display.widthPixels;

        sky = (RelativeLayout)findViewById(R.id.sky);

        launchPlane();
        spawnClouds();
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                spawn=false;
                Intent intent = new Intent(getApplicationContext(),LevelSelection.class);
                startActivity(intent);
            }
        }.start();

    }

    private void launchPlane() {
        ImageView plane = new ImageView(this);
        plane.setLayoutParams(new RelativeLayout.LayoutParams(300, 300));
        plane.setX(-300);
        plane.setY((height/2)-300);
        plane.setImageResource(R.drawable.ic_plane);
        sky.addView(plane);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator transXin = ObjectAnimator.ofFloat(plane, View.TRANSLATION_X,width/2-150);
        transXin.setStartDelay(500);
        transXin.setDuration(3500);
        transXin.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator transXout = ObjectAnimator.ofFloat(plane, View.TRANSLATION_X,width+150);
        transXout.setDuration(4000);
        transXout.setInterpolator(new AccelerateInterpolator());
        transXout.setStartDelay(2000);

        set.playSequentially(transXin,transXout);
        set.start();
    }

    private void spawnClouds() {
        rnd = new Random();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (spawn){
                    Log.d("happy", "run: ");
                    try {
                        Thread.sleep(400);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView cloud = new ImageView(getApplicationContext());
                                cloud.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
                                cloud.setX(width);
                                cloud.setY(rnd.nextInt(height));
                                cloud.setImageResource(R.drawable.ic_cloud);
                                sky.addView(cloud);
                                startAnimating(cloud);
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    private void startAnimating(ImageView cloud) {
        cloud.animate().x(-200).setDuration(7000).start();
    }
}
