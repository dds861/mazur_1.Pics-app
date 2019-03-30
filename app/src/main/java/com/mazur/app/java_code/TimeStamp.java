package com.mazur.app.java_code;

import android.content.Context;
import android.util.Log;
import com.mazur.app.ConstantsKt;

public class TimeStamp {

    private Context context;
    private Preferences preferences;

    public TimeStamp(Context context) {
        this.context = context;
        this.preferences = new Preferences(context);
    }

    private Long getCurrentTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private Long getOldTimeStamp() {
        Long oldTimeStamp = preferences.onGetLong(ConstantsKt.OLD_TIME_STAMP);
        //setting default period
        if (oldTimeStamp == -1) {
            setOldTimeStamp();
            return getCurrentTimeStamp() - 1;
        } else {

            return preferences.onGetLong(ConstantsKt.OLD_TIME_STAMP);
        }
    }

    public void setOldTimeStamp() {
        preferences.onSaveLong(ConstantsKt.OLD_TIME_STAMP, getCurrentTimeStamp());
    }

    public void setAdShowPeriod(Long adShowPeriod) {
        preferences.onSaveLong(ConstantsKt.AD_SHOW_PERIOD, adShowPeriod);
    }

    public Long getAdShowPeriod() {

        return preferences.onGetLong(ConstantsKt.AD_SHOW_PERIOD);
    }

    public boolean isTimeToShowAd() {

        Long oldTime = getOldTimeStamp();
        Long currentTime = getCurrentTimeStamp();
        Long passedTimeSinceLastAdShowing = currentTime - oldTime;
        Long adShowPeriod = getAdShowPeriod();

        if (passedTimeSinceLastAdShowing > adShowPeriod) {
            return true;
        }
        return false;
    }

    public boolean isTimeToShowWebView() {

        int displayTimes = preferences.onGetInt(ConstantsKt.INTERSTITIAL_AD_INCREMENTATION);
        Log.i("autolog", "TimeStamp: displayTimes: " + displayTimes);
        Long oldTime = getOldTimeStamp();
        Long currentTime = getCurrentTimeStamp();
        Long passedTimeSinceLastWebViewShowing = currentTime - oldTime;
        Log.i("autolog", "TimeStamp: passedTimeSinceLastWebViewShowing: " + passedTimeSinceLastWebViewShowing);
        Long adShowPeriod = getAdShowPeriod();
        Log.i("autolog", "TimeStamp: adShowPeriod: " + adShowPeriod);

        if (displayTimes == 5 && passedTimeSinceLastWebViewShowing > adShowPeriod) {
            displayTimes = 0;
            preferences.onSaveInt(ConstantsKt.INTERSTITIAL_AD_INCREMENTATION, displayTimes);
            setOldTimeStamp();
            return true;
        } else {
            return false;
        }
    }

    //Мусор
//    <script>
//        (
//            function(i, s, o, g, r, a, m) {
//                i[r] = i[r] || function() {
//                    (i[r].q = i[r].q ||[]).push(arguments)
//                     } ;
//                a = s.createElement(o),
//                m = s.getElementsByTagName(o)[0];
//                a.async = 1;
//                a.src = g;
//                m.parentNode.insertBefore(a, m)
//            }
//        )
//        (window,document,'script','http://cps.gold/js/embed.js?hash=HG35Idlh5w','wc');
//
//        wc('start','HG35Idlh5w', {});
//    </script>


//    <input type="button" value="Say hello" onClick="showAndroidToast('Hello Android!')" />
//
//<script type="text/javascript">
//    function showAndroidToast(toast) {
//        Android.showToast(toast);
//    }
//</script>
}
