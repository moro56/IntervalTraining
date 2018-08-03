package it.emperor.intervaltraining.di;

import org.codejargon.feather.Provides;

import io.realm.Realm;

public class DataModule {

    @Provides
    Realm provideRealm() {
        return Realm.getDefaultInstance();
    }
}
