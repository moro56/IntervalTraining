package it.emperor.intervaltraining.events;

import it.emperor.intervaltraining.models.Repetition;

public class EditRepetitionClickedEvent {

    private Repetition repetition;

    public EditRepetitionClickedEvent(Repetition repetition) {
        this.repetition = repetition;
    }

    public Repetition getRepetition() {
        return repetition;
    }
}
