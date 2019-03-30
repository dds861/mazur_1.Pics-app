package com.mazur.app.java_code;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class OpenGalleryListener implements View.OnClickListener {
    private Context context;
    private Uri savedImageURI;

    public OpenGalleryListener(Context context, Uri savedImageURI) {
        this.context = context;
        this.savedImageURI = savedImageURI;
    }

    @Override
    public void onClick(View view) {
        showPhoto(savedImageURI, view);
    }

    private void showPhoto(Uri photoUri, View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(photoUri, "image/*");
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
