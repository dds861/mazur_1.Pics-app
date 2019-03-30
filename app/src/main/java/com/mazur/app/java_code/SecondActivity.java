package com.mazur.app.java_code;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mazur.app.ConstantsKt;
import com.mazur.app.R;
import com.mazur.app.ui.splash.SplashActivity;

import java.util.List;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageView mMainIv;
    private FloatingActionButton mFab;
    private String TAG = "autolog";
    private CoordinatorLayout mLayoutRoot;
    private Image productDrawable;
    private TimeStamp timeStamp;

    private AdView mAdView;

    //    private InterstitialAd mInterstitialAd;
    private List<Image> images;
    private int position;
    //    private InterstitialAd mInterstitialAd;
    InterstitialAdmobAd interstitialAdmobAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();

        onLoadBannerAd();
//        onLoadInterstitialAd();
        onGetIntent();
        onShowImage();
        onRunWebView();


    }

    private void onRunWebView() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {

                    if (timeStamp.isTimeToShowWebView()) {
                        sleep(1000);
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        startActivity(intent);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        interstitialAdmobAd.onLoad();
    }

    private void onShowImage() {
        try {


            mMainIv.setImageDrawable(Firebase.images.get(position).getDrawable());
        } catch (IndexOutOfBoundsException e) {
            Log.i("autolog", "e: " + e);

        }
    }

    private void onGetIntent() {

        position = getIntent().getIntExtra("position", 0);

        Preferences preferences = new Preferences(getApplicationContext());
        int numberOfPics = preferences.onGetInt(ConstantsKt.NUMBER_OF_PICS) + 1;
        if (position == numberOfPics) {
            position = 0;
        }


        images = Firebase.images;
    }


    private void onLoadBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

//    private void onLoadInterstitialAd() {
//
//
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_ad_unit_id));
//
//
////        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//        mInterstitialAd.loadAd(adRequest);
//
//
//    }

//    private void onShowInterstitialAd() {
//
//
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//            Log.i("autolog", "The interstitial wasn't loaded yet.");
//        }
//    }

    private void displayIntersititalAd() {
        Preferences preferences = new Preferences(getApplicationContext());

        int displayTimes = preferences.onGetInt(ConstantsKt.INTERSTITIAL_AD_INCREMENTATION);
        Log.i("autolog", "SecondActivity: displayTimes: " + displayTimes);

        if (displayTimes == 2) {

            // Show interstitial
            interstitialAdmobAd.onShowInterstitialAd();
            displayTimes++;

        } else if (displayTimes == 5) {
            // Shown 6 times, reset counter
            interstitialAdmobAd.onShowInterstitialAd();
            displayTimes = 0;

        } else {
            displayTimes++;
        }


        preferences.onSaveInt(ConstantsKt.INTERSTITIAL_AD_INCREMENTATION, displayTimes);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        displayIntersititalAd();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                displayIntersititalAd();
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mMainIv = (ImageView) findViewById(R.id.iv_item_in_recyclerview);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mLayoutRoot = (CoordinatorLayout) findViewById(R.id.root_layout);
        mAdView = (AdView) findViewById(R.id.adView);
        timeStamp = new TimeStamp(getApplicationContext());
        MobileAds.initialize(getApplicationContext(),
                getResources().getString(R.string.admob_interstitial_tilda_ad_unit_id));

        interstitialAdmobAd = new InterstitialAdmobAd(getApplicationContext());

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (isWriteStoragePermissionGranted()) {
                    saveImageToGallery();
                }
                break;
            default:
                break;
        }
    }

    private void saveImageToGallery() {
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                ((BitmapDrawable) productDrawable.getDrawable()).getBitmap(),
                "Bird",
                "Image of bird");
        Uri savedImageURI = Uri.parse(savedImageURL);

        Snackbar mySnackbar = Snackbar.make(mLayoutRoot,
                "Downloaded", Snackbar.LENGTH_SHORT);
        mySnackbar.setAction("Open", new OpenGalleryListener(getApplicationContext(), savedImageURI));
        mySnackbar.show();
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    saveImageToGallery();
                } else {
                    Toast.makeText(this, "Permission to save is not granted", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    saveImageToGallery();
                } else {
                    Toast.makeText(this, "Permission to save is not granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

//        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
//        String taskUrl = remoteConfig.getString(ConstantsKt.TASK_URL);
//        String value = remoteConfig.getString(ConstantsKt.SHOW_IN);
//
//        if (value.equals(ConstantsKt.WEB_VIEW)) {
//            Intent intent = new Intent(this, WebViewActivity.class);
//            intent.putExtra(ConstantsKt.EXTRA_TASK_URL, taskUrl);
//            startActivity(intent);
//
//        } else if (value.equals(ConstantsKt.BROWSER)) {
//            // launch browser with task url
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(taskUrl));
//            startActivity(i);
//        }