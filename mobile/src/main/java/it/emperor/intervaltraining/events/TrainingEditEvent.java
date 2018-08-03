package it.emperor.intervaltraining.events;

public class TrainingEditEvent {

    private String id;
    private int color;

    public TrainingEditEvent(String id, int color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public int getColor() {
        return color;
    }
}
