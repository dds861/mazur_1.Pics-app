package com.mazur.app.ui.web_view;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

//данный клас нужен для работы с javascript на стороне сайте который будет отправлять данные сюда,
// а мы тут уже будем дальше обрабатывать данные
//пока данный класс не используется, но может понадобится в будущем
public class WebAppInterface {
    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void createElement(Object o) {
        Toast.makeText(mContext, "Событие", Toast.LENGTH_SHORT).show();
    }
}
