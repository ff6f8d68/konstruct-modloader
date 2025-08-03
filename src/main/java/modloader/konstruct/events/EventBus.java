package modloader.konstruct.events;

import java.lang.reflect.Method;
import java.util.*;

public class EventBus {
    private final Map<Class<?>, List<RegisteredHandler>> handlers = new HashMap<>();

    public void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubscribeEvent.class)
                    && method.getParameterCount() == 1
                    && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {

                Class<?> eventType = method.getParameterTypes()[0];
                SubscribeEvent meta = method.getAnnotation(SubscribeEvent.class);

                handlers
                        .computeIfAbsent(eventType, k -> new ArrayList<>())
                        .add(new RegisteredHandler(listener, method, meta.priority()));
            }
        }

        handlers.values().forEach(list ->
                list.sort(Comparator.comparing(r -> r.priority))
        );
    }
    private static final EventBus GLOBAL = new EventBus();

    public static EventBus getGlobal() {
        return GLOBAL;
    }
    public void post(Event event) {
        Class<?> clazz = event.getClass();
        while (clazz != null && Event.class.isAssignableFrom(clazz)) {
            List<RegisteredHandler> list = handlers.get(clazz);
            if (list != null) {
                for (RegisteredHandler r : list) {
                    try {
                        r.method.setAccessible(true);
                        r.method.invoke(r.owner, event);
                        if (event.isCancellable() && event.isCancelled()) {
                            return;  // stop if cancelled
                        }
                    } catch (Exception e) {
                        System.err.println("[Konstruct/EventBus] Error in handler: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }


    private record RegisteredHandler(Object owner, Method method, EventPriority priority) {}
}
