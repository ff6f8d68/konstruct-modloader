package modloader.konstruct.events;

import java.util.Map;

public class NamedEvent extends Event {
    private final String name;
    private final Map<String, Object> data;

    public NamedEvent(String name, Map<String, Object> data) {
        this.name = name;
        this.data = data;
    }

    public NamedEvent(String name) {
        this(name, Map.of());
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public boolean isCancellable() {
        return false; // Or true if you want to support cancellation dynamically
    }
}
