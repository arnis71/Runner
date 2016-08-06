package com.arnis.tt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arnis.tt.activities.ContinentSelection;
import com.arnis.tt.activities.LevelSelection;
import com.arnis.tt.activities.LoadingClouds;

/**
 * Created by arnis on 16.07.2016.
 */
public class LevelSelectionHelper {
    LevelSelection levelSelection;
    String currentContinent;

    public String getCurrentCountry() {
        return countries[currentCountry];
    }

    int currentCountry;
    int avaliableTill=1;
    String[] countries;
    public static final String PLACES_DB = "places";
    public static final String CURRENT_COUNTRY = "curr_country";

    public LevelSelectionHelper(LevelSelection levelSelection) {
        this.levelSelection=levelSelection;
        countries = levelSelection.getResources().getStringArray(R.array.InEurope);
        currentCountry = levelSelection.getSharedPreferences(PLACES_DB, Context.MODE_PRIVATE).getInt(CURRENT_COUNTRY,0);
    }

    public void handleIncomingIntent(Intent intent) {
        if (intent.getExtras()!=null){
            currentContinent = intent.getExtras().getString(ContinentSelection.CONTINENT);
        }
    }

    public void setCurrentCountry(TextView bar) {
        bar.setText(countries[currentCountry]);

    }

    public void nextCountry(){
        currentCountry++;
        levelSelection.getSharedPreferences(PLACES_DB,Context.MODE_PRIVATE).edit().putInt(CURRENT_COUNTRY,currentCountry).apply();
    }
    public void prevCountry(){
        if (currentCountry>=0){
            currentCountry--;
            levelSelection.getSharedPreferences(PLACES_DB,Context.MODE_PRIVATE).edit().putInt(CURRENT_COUNTRY,currentCountry).apply();
        }
    }

    public void toggleFooter(RelativeLayout footer) {
        if (currentCountry>0){
            footer = (RelativeLayout) levelSelection.findViewById(R.id.footer_country);
            TextView footerText = (TextView)footer.findViewById(R.id.footer_text);
            footerText.setText(countries[currentCountry-1]);
            footer.setVisibility(View.VISIBLE);
            footer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prevCountry();
                    Intent intent = new Intent(levelSelection, LoadingClouds.class);
                    levelSelection.startActivity(intent);
                }
            });
        }
    }

    public boolean nextCountryAvaliable() {
        if (currentCountry+1<=avaliableTill)
            return true;
        return false;
    }

    public void toggleUpBar(TextView upBar) {
        upBar.setY(-upBar.getHeight());
        upBar.setText(countries[currentCountry+1]);
        upBar.setVisibility(View.VISIBLE);
    }

    public void getCircleX(){

    }
}
