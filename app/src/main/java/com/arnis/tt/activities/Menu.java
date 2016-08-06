package com.arnis.tt.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.arnis.tt.base.Coins;
import com.arnis.tt.costumes.Costume;
import com.arnis.tt.Firebase;
import com.arnis.tt.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Menu extends AppCompatActivity {




    private int vkID;
    public static Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        System.out.println(Arrays.asList(fingerprints));
//        FirebaseCrash.log("SQL database failed to initialize");
//        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
//        firebase = new Firebase(this);
//
//
//        firebase.createBundle().put("New game","Start").logEvent(FirebaseAnalytics.Event.APP_OPEN);

        Costume.initCostumes();
        Costume.setAvaliable(getSharedPreferences(Costume.COSTUMES_DB,MODE_PRIVATE));
        Costume.setActive(getSharedPreferences(Costume.COSTUMES_DB,MODE_PRIVATE));

        MyPassport.coins = new Coins(getSharedPreferences(Coins.COINS_DB,MODE_PRIVATE));
    }



    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(getApplicationContext(),"fuckyeah",Toast.LENGTH_SHORT).show();
                final VKRequest vkRequest = VKApi.users().get();
                vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            JSONArray jsonArray = (JSONArray) response.json.get("response");
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                            vkID = Integer.parseInt(jsonObject.getString("id"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(),"fuckno",Toast.LENGTH_SHORT).show();
                // User didn't pass Authorization
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void Play(View view) {
        Intent intent = new Intent(this,ContinentSelection.class);
        startActivity(intent);
    }


    public void vkAuth(View view) {
        VKSdk.login(this, VKScope.WALL);
    }

    public void vkMakePost(View view) {

        String message = "This is a post message";


        VKParameters vkParameters = new VKParameters();
        vkParameters.put(VKApiConst.OWNER_ID,vkID);
        vkParameters.put(VKApiConst.MESSAGE,message);
        VKRequest post = VKApi.wall().post(vkParameters);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                System.out.println(response.responseString);
                MyPassport.coins.assignCoins(1000);
                //coinsAmount.setText(Integer.toString(coins.getAmount()));
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                System.out.println(error);
            }
        });

    }

    public void myPassport(View view) {
        Intent intent = new Intent(this,MyPassport.class);
        startActivity(intent);
    }


}
