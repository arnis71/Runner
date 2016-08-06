package com.arnis.tt.activities;

import com.vk.sdk.VKSdk;

/**
 * Created by arnis on 12.07.2016.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
