package com.client.omise;

import android.content.Context;
import android.content.SharedPreferences;

public class KeyManager {
    private final String KEY_PREFS = "prefs_user";
    private final String PUB_KEY = "pub_key";
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    public KeyManager(Context context) {
        mPrefs = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public String getPublicKey() {
        return mPrefs.getString(PUB_KEY, "");
    }

    public boolean registerPubKey(String pubkey) {
        return mEditor.putString(PUB_KEY, pubkey).commit();
    }
}

