package com.mazur.app.java_code;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Preferences(Context context) {
        this.context = context;

    }

    public void onSaveInt(String key, int dataToSave) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Save counter back to SharedPreferences
        editor.putInt(key, dataToSave);
        editor.apply();
    }

    public int onGetInt(String key) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

  public void onSaveLong(String key, Long dataToSave) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Save counter back to SharedPreferences
        editor.putLong(key, dataToSave);
        editor.apply();
    }

    public Long onGetLong(String key) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return preferences.getLong(key, -1);
    }

    public void onSaveBoolean(String key, boolean dataToSave) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Save counter back to SharedPreferences
        editor.putBoolean(key, dataToSave);
        editor.apply();
    }

    public boolean onGetBoolean(String key) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return preferences.getBoolean(key,false);
    }

    public void onSaveString(String key, String dataToSave) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Save counter back to SharedPreferences
        editor.putString(key, dataToSave);
        editor.apply();
    }


    public String onGetString(String key) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return preferences.getString(key, "empty");
    }
}
