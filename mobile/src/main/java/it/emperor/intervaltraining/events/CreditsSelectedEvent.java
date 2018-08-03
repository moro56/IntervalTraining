package it.emperor.intervaltraining.events;

public class CreditsSelectedEvent {

    private String url;

    public CreditsSelectedEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
