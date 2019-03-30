package com.mazur.app.java_code;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazur.app.BuildConfig;
import com.mazur.app.ConstantsKt;
import com.mazur.app.R;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Firebase extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Context context;
    private IContentActivity iContentActivity;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    static List<Image> images;

    public Firebase(Context context) {
        this.context = context;
        this.iContentActivity = (IContentActivity) context;

    }

    public void downloadFile() {

        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();

        // [START download_to_local_file]

        final Preferences preferences = new Preferences(context);
        if (preferences.onGetInt(ConstantsKt.STORAGE_REFERENCE_NAME) == 0) {
            preferences.onSaveInt(ConstantsKt.STORAGE_REFERENCE_NAME, 1);
        }

        StorageReference islandRef = storageRef.child("images1/images" + preferences.onGetInt(ConstantsKt.STORAGE_REFERENCE_NAME) + ".zip");


        final File localFile = new File(context.getCacheDir().getPath(), "images1.zip");


        islandRef.getFile(localFile).

                addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i("autolog", "Downloaded file from Firebase: ");
                        // Local temp file has been created

                        Thread t1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                unpackZip(localFile.getParent() + "/", localFile.getName());
                            }
                        }, "t1");

                        t1.start();
                        try {
                            t1.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getImagesFromCache();
                        iContentActivity.onLoadRecyclerView(images);
                        Preferences preferences = new Preferences(context);
                        preferences.onSaveBoolean(ConstantsKt.PICS_UPLOADED, true);
                    }
                }).

                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i("autolog", "exception: " + exception);
                        Log.i("autolog", "Failed to Downloaded file from Firebase: ");
//                        preferences.onSaveInt(preferences.onGetInt() + 1);
//                        downloadFile();
                    }
                });
        // [END download_to_local_file]


    }


    private boolean unpackZip(String path, String name) {
        Log.i("autolog", "unpackZip path: " + path);

        int numberOfPics = 0;

        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path + name);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
                numberOfPics++;
            }

            zis.close();
        } catch (IOException e) {
            Log.i("autolog", "e: " + e);
            e.printStackTrace();

            return false;
        }

        Log.i("autolog", "Unzipped");

        Preferences preferences = new Preferences(context);
        preferences.onSaveInt(ConstantsKt.NUMBER_OF_PICS, numberOfPics);


        return true;
    }


    public void getImagesFromCache() {
        List<Image> images = new ArrayList<>();


        String pathName = context.getCacheDir().getPath();
        Log.i("autolog", "pathName: " + pathName);

        Preferences preferences = new Preferences(context);

        int numberOfPics = preferences.onGetInt(ConstantsKt.NUMBER_OF_PICS) + 1;
        Drawable drawable = null;
        for (int j = 1; j <= numberOfPics; j++) {
            String tempPath = "";
            if (j < 10) {
                tempPath = pathName + "/pic_00" + j + ".jpg";
                drawable = Drawable.createFromPath(tempPath);

            } else if (j > 9 && j < 100) {
                tempPath = pathName + "/pic_0" + j + ".jpg";
                drawable = Drawable.createFromPath(tempPath);

            } else if (j > 99) {
                tempPath = pathName + "/pic_" + j + ".jpg";
                drawable = Drawable.createFromPath(tempPath);
            }
            images.add(new Image(drawable));
        }

        Collections.shuffle(images);
        this.images = images;
//        ImageList.setImages(images);

    }

    private static Drawable convertToDrawable(Context context, Bitmap bitmap) {
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }


    public Long getTimeStamp() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.default_parameters);

        fetchPeriod();

        return mFirebaseRemoteConfig.getLong(ConstantsKt.AD_SHOW_PERIOD_IN_FIREBASE);
    }

    private void fetchPeriod() {


        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                        }

                    }
                });
        // [END fetch_config_with_callback]
    }


}
