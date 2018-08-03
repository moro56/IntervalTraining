package it.emperor.intervaltraining;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import net.danlew.android.joda.JodaTimeAndroid;

import org.codejargon.feather.Feather;

import io.fabric.sdk.android.Fabric;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import it.emperor.intervaltraining.di.DataModule;
import it.emperor.intervaltraining.utility.Prefs;

public class App extends MultiDexApplication {

    private static App instance;
    private static Feather feather;

    public App() {
        instance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new Answers());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        feather = Feather.with(new DataModule());
        JodaTimeAndroid.init(this);

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .schemaVersion(0)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                    }
                })
                .build());

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .setDefaultIntValue(-1)
                .setDefaultBooleanValue(false)
                .setDefaultStringValue(null)
                .build();
    }

    public static Context context() {
        return instance.getApplicationContext();
    }

    public static Feather feather() {
        return feather;
    }
}
