package it.emperor.intervaltraining.di;

import org.codejargon.feather.Provides;

import javax.inject.Singleton;

import it.emperor.intervaltraining.providers.SoundProvider;

public class ProviderModule {

    @Provides
    @Singleton
    SoundProvider provideSound() {
        return new SoundProvider();
    }
}
