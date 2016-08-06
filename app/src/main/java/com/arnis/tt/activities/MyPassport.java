package com.arnis.tt.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.arnis.tt.R;
import com.arnis.tt.base.Coins;
import com.arnis.tt.costumes.Costume;

public class MyPassport extends AppCompatActivity {

    private CheckBox autoMove;
    private boolean autoMoving;
    private ViewAnimator viewAnimator;
    public static final String AUTOMOVING = "AUTOMOVING";
    public static final String DB = "prefs";
    private SharedPreferences preferences;
    private TextView coinsAmount;
    public static Coins coins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_passport);

        autoMove = (CheckBox)findViewById(R.id.auto_move);
        coinsAmount = (TextView)findViewById(R.id.coins_amount);
        coinsAmount.setText(Integer.toString(coins.getAmount()));
        coinsAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coins.assignCoins(100);
                coinsAmount.setText(Integer.toString(coins.getAmount()));
            }
        });

        viewAnimator = (ViewAnimator)findViewById(R.id.viewAnimator);
        populateViewAnimator();
        viewAnimator.setDisplayedChild(Costume.getActiveCostume().id-1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        coinsAmount.setText(Integer.toString(coins.getAmount()));
        preferences = getSharedPreferences(DB,MODE_PRIVATE);
        autoMove.setChecked(preferences.getBoolean(AUTOMOVING,true));
        autoMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked())
                    autoMoving=true;
                else autoMoving=false;
                preferences = getSharedPreferences(DB,MODE_PRIVATE);
                preferences.edit().putBoolean(AUTOMOVING,autoMoving).apply();
            }
        });


    }

    @Override
    protected void onPause() {
        preferences = getSharedPreferences(Costume.COSTUMES_DB,MODE_PRIVATE);
        preferences.edit().putInt(Costume.CURRENT_COSTUME,Costume.getActiveCostume().id).apply();

        super.onPause();
    }

    public void costumeLeft(View view) {
        viewAnimator.showPrevious();
    }

    public void costumeRight(View view) {
        viewAnimator.showNext();
    }

    private void populateViewAnimator() {
        viewAnimator.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        viewAnimator.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        for (final Costume c: Costume.costumes){
            View v = LayoutInflater.from(this).inflate(R.layout.costume_layout,null);
            c.look = v;
            v.setLayoutParams(new ViewAnimator.LayoutParams(ViewAnimator.LayoutParams.WRAP_CONTENT, ViewAnimator.LayoutParams.WRAP_CONTENT));
            final ImageView iv = (ImageView) v.findViewById(R.id.costume_look);
            iv.setImageResource(c.getDrawableID());
            if (c.isActive())
                v.findViewById(R.id.costume_check_current_active).setVisibility(View.VISIBLE);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!c.isAvaliable()){
                        c.purchase(getSharedPreferences(Costume.COSTUMES_DB,MODE_PRIVATE));
                        iv.setImageResource(c.getDrawableID());
                        coinsAmount.setText(Integer.toString(coins.getAmount()));
                    }
                    else {
                        if (!c.isActive())
                            c.setActive(true);
                    }
                }
            });
            viewAnimator.addView(v);
        }
    }
}
