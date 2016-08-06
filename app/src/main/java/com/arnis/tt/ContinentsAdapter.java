package com.arnis.tt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arnis on 16.07.2016.
 */
public class ContinentsAdapter extends BaseAdapter {
    String[] continents;
    Context context;

    public ContinentsAdapter(Context context, String[] stringArray) {
        this.context=context;
        this.continents = new String[stringArray.length];
        this.continents = stringArray;
    }

    @Override
    public int getCount() {
        return continents.length;
    }

    @Override
    public Object getItem(int i) {
        return continents[i];
    }

    @Override
    public long getItemId(int i) {
        return continents[i].hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.continents_card,viewGroup,false);
        }
        TextView text = (TextView) view.findViewById(R.id.continent_card);
        text.setText(continents[i]);


        return view;
    }
}
