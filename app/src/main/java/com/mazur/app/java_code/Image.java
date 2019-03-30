package com.mazur.app.java_code;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Image implements Serializable {
   private Drawable drawable;

    public Image(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
