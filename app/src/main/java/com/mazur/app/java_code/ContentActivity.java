package com.mazur.app.java_code;

import android.app.Application;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mazur.app.ConstantsKt;
import com.mazur.app.R;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import java.util.List;

public class ContentActivity extends AppCompatActivity implements IContentActivity {

    private ImageView mImageFromRecyclerviewIv;
    private AppEventsLogger logger;


    private AdView mAdView;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private ProgressBar mBarProgress;
    private Firebase firebase;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initView();

        onDownloadPics();
        onUpdateAdShowPeriod();
        onLoadAdmobBannerAd();
        onLoadYandexMetrics();
        onLogFacebookEvent();
        onItemRecyclerViewClickListerner();

    }

    private void onDownloadPics() {
        boolean isPicsUploaded = preferences.onGetBoolean(ConstantsKt.PICS_UPLOADED);
        if (!isPicsUploaded) {
            firebase.downloadFile();
        } else {
            firebase.getImagesFromCache();
            onLoadRecyclerView(Firebase.images);
        }
    }

    private void onUpdateAdShowPeriod() {
        TimeStamp timeStamp = new TimeStamp(getApplicationContext());
        Long newAdShowPeriod = firebase.getTimeStamp();
        Log.i("autolog", "ContentActivity: newAdShowPeriod: " + newAdShowPeriod);
        timeStamp.setAdShowPeriod(newAdShowPeriod);
    }

    private void onItemRecyclerViewClickListerner() {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);

            }
        });
    }

    private void initView() {
        mImageFromRecyclerviewIv = (ImageView) findViewById(R.id.iv_item_in_recyclerview);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        mAdView = (AdView) findViewById(R.id.adView);
        mBarProgress = (ProgressBar) findViewById(R.id.progress_bar);
        preferences = new Preferences(getApplicationContext());
        mBarProgress.setVisibility(View.VISIBLE);
        mAdView.setVisibility(View.GONE);
        firebase = new Firebase(this);
    }

    public void onLogFacebookEvent() {
        logger = AppEventsLogger.newLogger(getApplicationContext());
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT);
    }

    @Override
    public void onLoadRecyclerView(final List<Image> images) {

        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(4));

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstItemVisible = linearLayoutManager.findFirstVisibleItemPosition();
                if (firstItemVisible != 0 && firstItemVisible % images.size() == 0) {
                    recyclerView.getLayoutManager().scrollToPosition(0);
                }
            }
        });


        myAdapter = new MyAdapter(this, images);
        recyclerView.setAdapter(myAdapter);
        mBarProgress.setVisibility(View.GONE);
        mAdView.setVisibility(View.VISIBLE);
    }


    private void onLoadAdmobBannerAd() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    private void onLoadYandexMetrics() {

//        String API_key = "71541371-3720-4a88-b4e8-8362f9ec5a4a";
        String API_key = getResources().getString(R.string.yandex_api_key);
        // Создание расширенной конфигурации библиотеки.
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key).build();
        // Инициализация AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Отслеживание активности пользователей.
        YandexMetrica.enableActivityAutoTracking((Application) getApplicationContext());
    }

    public void autoScroll() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollBy(2, 0);
                handler.postDelayed(this, 0);
            }
        };
        handler.postDelayed(runnable, 0);
    }


    public class RecyclerViewItemDecorator extends RecyclerView.ItemDecoration {
        private int spaceInPixels;

        public RecyclerViewItemDecorator(int spaceInPixels) {
            this.spaceInPixels = spaceInPixels;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
//            outRect.left = spaceInPixels;
//            outRect.right = spaceInPixels;
            outRect.bottom = spaceInPixels;
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = spaceInPixels;
            } else {
                outRect.top = 0;
            }
        }
    }


    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh_button:
                firebase.downloadFile();
                mBarProgress.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
