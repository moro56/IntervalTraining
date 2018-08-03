package it.emperor.intervaltraining.utility;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;

/**
 * Shared Preferences
 * OnePrefs (https://github.com/swolfand/OnePrefs)
 */
public class Prefs {

    public static String mName;
    private static final String DEFAULT_SUFFIX = "_preferences";
    private static final String LENGTH = "#LENGTH";
    private static SharedPreferences mPref;
    private static String mDefStringValue;
    private static Integer mDefIntValue;
    private static Boolean mDefBooleanValue;

    private static void initPrefs(Context context, String name, int mode) {
        mName = name;
        mPref = context.getSharedPreferences(name, mode);
    }

    public static SharedPreferences getPreferences() {
        if (mPref != null) {
            return mPref;
        }
        throw new RuntimeException(
                "Prefs class is null!"
        );
    }

    public static Map<String, ?> getAll() {
        return getPreferences().getAll();
    }

    public static int getInt(final String key) {
        if (mDefIntValue == null) {
            Log.e("Null Val", "Default value not set");
            throw new NullPointerException("You did not set a default value, please refer to documentation");
        }
        return getPreferences().getInt(key, mDefIntValue);
    }

    public static int getInt(final String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static boolean getBoolean(final String key) {
        if (mDefBooleanValue == null) {
            Log.e("Null Val", "Default value not set");
            throw new NullPointerException("You did not set a default value, please refer to documentation");
        }
        return getPreferences().getBoolean(key, mDefBooleanValue);
    }

    public static boolean getBoolean(final String key, final boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static long getLong(final String key, final long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static long getLong(final String key) {
        return getPreferences().getLong(key, mDefIntValue);
    }

    public static double getDouble(final String key, final double defValue) {
        return Double.longBitsToDouble(getPreferences().getLong(key, Double.doubleToLongBits(defValue)));
    }

    public static double getDouble(final String key) {
        return Double.longBitsToDouble(getPreferences().getLong(key, Double.doubleToLongBits(mDefIntValue)));
    }

    public static float getFloat(final String key, final float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static float getFloat(final String key) {
        if (mDefIntValue == null) {
            Log.e("Null Val", "Default value not set");
            throw new NullPointerException("You did not set a default int value, please refer to documentation");
        }
        return getPreferences().getFloat(key, mDefIntValue);
    }

    public static String getString(final String key, final String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static String getString(final String key) {
        return getPreferences().getString(key, mDefStringValue);
    }

    public static void putLong(final String key, final long value) {
        final SharedPreferences.Editor editor = edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void putInt(final String key, final int value) {
        final SharedPreferences.Editor editor = edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putDouble(final String key, final double value) {
        final SharedPreferences.Editor editor = edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.apply();
    }

    public static void putFloat(final String key, final float value) {
        final SharedPreferences.Editor editor = edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void putBoolean(final String key, final boolean value) {
        final SharedPreferences.Editor editor = edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putString(final String key, final String value) {
        final SharedPreferences.Editor editor = edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void remove(final String key) {
        SharedPreferences prefs = getPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(key + LENGTH)) {
            // Workaround for pre-HC's lack of StringSets
            int stringSetLength = prefs.getInt(key + LENGTH, -1);
            if (stringSetLength >= 0) {
                editor.remove(key + LENGTH);
                for (int i = 0; i < stringSetLength; i++) {
                    editor.remove(key + "[" + i + "]");
                }
            }
        }
        editor.remove(key);

        editor.apply();
    }

    public static boolean contains(final String key) {
        return getPreferences().contains(key);
    }

    public static SharedPreferences.Editor clear() {
        final SharedPreferences.Editor editor = edit().clear();
        editor.apply();
        return editor;
    }

    public static SharedPreferences.Editor edit() {
        return getPreferences().edit();
    }

    public final static class Builder {
        private String mKey;
        private Context mContext;
        private int mMode = -1;
        private boolean mUseDefault = false;

        public Builder setPrefsName(final String prefsName) {
            mKey = prefsName;
            return this;
        }

        public Builder setContext(final Context context) {
            mContext = context;
            return this;
        }

        @SuppressWarnings("deprecation")
        public Builder setMode(final int mode) {
            if (mode == ContextWrapper.MODE_PRIVATE || mode == ContextWrapper.MODE_WORLD_READABLE || mode == ContextWrapper.MODE_WORLD_WRITEABLE || mode == ContextWrapper.MODE_MULTI_PROCESS) {
                mMode = mode;
            } else {
                throw new RuntimeException("The mode in the sharedpreference can only be set too ContextWrapper.MODE_PRIVATE, ContextWrapper.MODE_WORLD_READABLE, ContextWrapper.MODE_WORLD_WRITEABLE or ContextWrapper.MODE_MULTI_PROCESS");
            }

            return this;
        }

        public Builder setUseDefaultSharedPreference(boolean defaultSharedPreference) {
            mUseDefault = defaultSharedPreference;
            return this;
        }

        public Builder setDefaultIntValue(final int defaultIntValue) {
            mDefIntValue = defaultIntValue;
            return this;
        }

        public Builder setDefaultBooleanValue(final boolean defaultBooleanValue) {
            mDefBooleanValue = defaultBooleanValue;
            return this;
        }

        public Builder setDefaultStringValue(final String defaultStringValue) {
            mDefStringValue = defaultStringValue;
            return this;
        }

        public void build() {
            if (mContext == null) {
                throw new RuntimeException("Context not set, please set context before building the Prefs instance.");
            }

            if (TextUtils.isEmpty(mKey)) {
                mKey = mContext.getPackageName();
            }

            if (mUseDefault) {
                mKey += DEFAULT_SUFFIX;
            }

            if (mMode == -1) {
                mMode = ContextWrapper.MODE_PRIVATE;
            }

            Prefs.initPrefs(mContext, mKey, mMode);
        }
    }
}
