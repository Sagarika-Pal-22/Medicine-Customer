package myrehabcare.in.Service;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private static SharedPreference pref;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    public static final String NAME = "PdfViewer.pref";
    private SharedPreference(Context ctx) {
        sharedPreference = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }

    public static SharedPreference getInstance(Context ctx) {
        if (pref == null) {
            pref = new SharedPreference(ctx);
        }
        return pref;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, long def) {
        return sharedPreference.getLong(key, -1l);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putFloat(String key, Float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        return sharedPreference.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreference.getBoolean(key, defValue);
    }

    public int getInteger(String key, int defValue) {
        return sharedPreference.getInt(key, defValue);
    }

    public Float getFloat(String key, Float defValue) {
        return sharedPreference.getFloat(key, defValue);
    }

    public void deleteAllPreference() {
        editor.clear();
        editor.commit();
    }

    public void deletePreference(String key) {
        editor.remove(key);
        editor.commit();
    }

}

