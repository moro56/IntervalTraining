package it.emperor.intervaltraining.events;

public class TrainingDeleteEvent {

    private String id;

    public TrainingDeleteEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
