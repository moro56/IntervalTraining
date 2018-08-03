package it.emperor.intervaltraining.models.converter;

import android.os.Parcel;

import org.parceler.Parcels;

import it.emperor.intervaltraining.models.Repetition;

public class RepetitionListParcelConverter extends RealmListParcelConverter<Repetition> {

    @Override
    public void itemToParcel(Repetition input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public Repetition itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Repetition.class.getClassLoader()));
    }
}