package it.emperor.intervaltraining.events;

public class TrainingSelectedEvent {

    private String id;

    public TrainingSelectedEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
