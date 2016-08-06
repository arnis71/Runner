package com.arnis.tt.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.arnis.tt.ContinentsAdapter;
import com.arnis.tt.R;

public class ContinentSelection extends AppCompatActivity {

    public static final String CONTINENT = "CONTINENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continent_selection);


        ListView continentsList = (ListView)findViewById(R.id.continents_list);
        final ContinentsAdapter continentsAdapter = new ContinentsAdapter(this,getResources().getStringArray(R.array.continents));
        continentsList.setAdapter(continentsAdapter);

        continentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),LevelSelection.class);
                intent.putExtra(CONTINENT,(String)continentsAdapter.getItem(i));
                startActivity(intent);
            }
        });
    }
}
