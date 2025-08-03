package modloader.konstruct.events;

public abstract class Event {
    private boolean cancelled = false;

    public boolean isCancellable() {
        return false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        if (isCancellable()) {
            this.cancelled = true;
        }
    }
}
