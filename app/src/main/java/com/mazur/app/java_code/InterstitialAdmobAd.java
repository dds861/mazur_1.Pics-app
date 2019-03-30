package com.mazur.app.java_code;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.ads.AdRequest;
import com.mazur.app.R;

public class InterstitialAdmobAd {

    private Context context;
    private com.google.android.gms.ads.InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    public InterstitialAdmobAd(Context context) {
        this.context = context;
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.admob_interstitial_ad_unit_id));

//        AdRequest adRequest = new AdRequest.Builder().build();
        adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

    }

    public void onLoad() {
        mInterstitialAd.loadAd(adRequest);
    }


    public void onShowInterstitialAd() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.i("autolog", "The interstitial wasn't loaded yet.");
        }
    }
}
