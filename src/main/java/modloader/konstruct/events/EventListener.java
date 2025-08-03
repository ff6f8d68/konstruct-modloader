package modloader.konstruct.events;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
    EventPriority priority() default EventPriority.NORMAL;
}
