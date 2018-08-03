package it.emperor.intervaltraining.models;

import org.parceler.Parcel;
import org.parceler.ParcelProperty;
import org.parceler.ParcelPropertyConverter;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.TrainingRealmProxy;
import io.realm.annotations.PrimaryKey;
import it.emperor.intervaltraining.models.converter.RepetitionListParcelConverter;
import it.emperor.intervaltraining.utility.Constants;

@Parcel(implementations = {TrainingRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Training.class})
public class Training extends RealmObject {

    @PrimaryKey
    private String id;

    private String title;
    private int color;
    private int prepareTime;
    private int cooldownTime;
    private int numRepetition;
    private RealmList<Repetition> repetitions;

    private long createdAt;
    private long updatedAt;

    public Training() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        if (color == 0) {
            return Constants.COLOR_RED;
        }
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPrepareTime() {
        return prepareTime;
    }

    public void setPrepareTime(int prepareTime) {
        this.prepareTime = prepareTime;
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    public void setCooldownTime(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public int getNumRepetition() {
        return numRepetition;
    }

    public void setNumRepetition(int numRepetition) {
        this.numRepetition = numRepetition;
    }

    public RealmList<Repetition> getRepetitions() {
        if (repetitions == null)
            repetitions = new RealmList<>();
        return repetitions;
    }

    @ParcelProperty("repetitions")
    @ParcelPropertyConverter(RepetitionListParcelConverter.class)
    public void setRepetitions(RealmList<Repetition> repetitions) {
        this.repetitions = repetitions;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        setUpdatedAt(createdAt);
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // STATIC

    public static Training getTrainingById(Realm realm, String id) {
        return realm.where(Training.class).equalTo("id", id).findFirst();
    }

    public static RealmResults<Training> getAllTraining(Realm realm) {
        return realm.where(Training.class).findAllSorted("updatedAt", Sort.DESCENDING);
    }
}
