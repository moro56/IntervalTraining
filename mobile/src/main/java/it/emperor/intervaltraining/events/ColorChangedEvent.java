package it.emperor.intervaltraining.events;

public class ColorChangedEvent {

    private int position;
    private int offsetX;

    public ColorChangedEvent(int position, int offsetX) {
        this.position = position;
        this.offsetX = offsetX;
    }

    public int getPosition() {
        return position;
    }

    public int getOffsetX() {
        return offsetX;
    }
}
