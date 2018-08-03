package it.emperor.intervaltraining.events;

public class NumberTrainingsChangedEvent {

    private int newSize;

    public NumberTrainingsChangedEvent(int newSize) {
        this.newSize = newSize;
    }

    public int getNewSize() {
        return newSize;
    }
}
