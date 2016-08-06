package com.arnis.tt.costumes;

import com.arnis.tt.R;

/**
 * Created by arnis on 10.07.2016.
 */
public class Basic extends Costume {
    public Basic() {
        this.id = Costume.BASIC;
        this.cost=0;
        this.setAvaliable(true);
        this.drawableID = R.drawable.player_icon;
    }
}
