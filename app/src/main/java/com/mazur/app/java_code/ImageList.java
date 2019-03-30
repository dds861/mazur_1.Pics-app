package com.mazur.app.java_code;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ImageList implements Serializable {

    static List<Image> images;


    //нужен в том случае если необходимо прочитать картинки из drawables
    public static List<Image> getList(Context context) {
        images = new ArrayList<>();

        int numberOfPics = 3;
//        int numberOfPics = Integer.valueOf(context.getResources().getString(R.string.number_of_pics));
        Drawable drawable = null;
        for (int j = 1; j <= numberOfPics; j++) {
            if (j < 10) {
                drawable = context.getResources().getDrawable(context.getResources()
                        .getIdentifier("pic_00" + j, "drawable", context.getPackageName()));
            } else if (j > 9 && j < 100) {
                drawable = context.getResources().getDrawable(context.getResources()
                        .getIdentifier("pic_0" + j, "drawable", context.getPackageName()));
            } else if (j > 99) {
                drawable = context.getResources().getDrawable(context.getResources()
                        .getIdentifier("pic_" + j, "drawable", context.getPackageName()));
            }

            drawable = rotateImage(context, drawable);
            images.add(new Image(drawable));

        }


        return images;
    }



    //если буде хранить в drawables картинки перевернутыми на -90 градусов, будем переворачивать картинки
    //хранить перевернутыми необходимо для уникализации картинок
    private static Drawable rotateImage(Context context, Drawable drawable) {
        Matrix matrix = new Matrix();

        matrix.postRotate(90);
        Bitmap myLogo = convertToBitmap(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myLogo, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        drawable = convertToDrawable(context, rotatedBitmap);

        return drawable;
    }


    private static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    private static Drawable convertToDrawable(Context context, Bitmap bitmap) {
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }


    public static List<Image> getImages() {
        return images;
    }

    public static void setImages(List<Image> list) {
        images = list;
    }
}
