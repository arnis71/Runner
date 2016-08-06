package com.arnis.tt;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by arnis on 13.07.2016.
 */
public class Firebase {
    private FirebaseAnalytics analytics;
    private Bundle bundle;

    public FirebaseAnalytics getAnalytics() {
        return analytics;
    }
    public Firebase(Context context) {
        analytics = FirebaseAnalytics.getInstance(context);
    }

    public Firebase createBundle(){
        bundle = new Bundle();
        return this;
    }

    public Firebase put(String param,String data){
        bundle.putString(param,data);
        return this;
    }

    public void logEvent(String event){
        analytics.logEvent(event,bundle);
    }
}
