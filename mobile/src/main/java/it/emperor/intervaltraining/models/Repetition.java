package it.emperor.intervaltraining.models;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.RepetitionRealmProxy;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {RepetitionRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Repetition.class})
public class Repetition extends RealmObject {

    @PrimaryKey
    private String repetitionId;

    private String trainingId;
    private int workTime;
    private int restTime;
    private int count;
    private int order;

    public Repetition() {
    }

    public String getRepetitionId() {
        return repetitionId;
    }

    public void setRepetitionId(String repetitionId) {
        this.repetitionId = repetitionId;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
